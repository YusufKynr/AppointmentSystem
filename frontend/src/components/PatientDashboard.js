import React, { useState, useEffect } from 'react';
import { userAPI, appointmentAPI } from '../services/api';
import './PatientDashboard.css';

const PatientDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('appointments');
  const [appointments, setAppointments] = useState([]);
  const [availableDoctors, setAvailableDoctors] = useState([]);
  const [filteredDoctors, setFilteredDoctors] = useState([]);
  const [appointmentForm, setAppointmentForm] = useState({
    specialty: '',
    doctorId: '',
    date: '',
    time: '',
    notes: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const specialties = [
    { value: 'Dermatology', label: 'Dermatoloji' },
    { value: 'Cardiology', label: 'Kardiyoloji' },
    { value: 'Eye', label: 'Göz Hastalıkları' },
    { value: 'General_Surgery', label: 'Genel Cerrahi' }
  ];

  // Database'den doktorları ve randevuları yükle
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setError('');
        
        // Doktorları database'den getir
        const doctors = await userAPI.getAllDoctors();
        
        // Frontend için doktor bilgilerini düzenle
        const mappedDoctors = doctors.map(doctor => ({
          id: doctor.userId,
          name: `${doctor.name} ${doctor.surname}`,
          email: doctor.email,
          role: 'doktor',
          specialty: doctor.specialty,
          availability: doctor.availability,
          birthDate: doctor.birthDate,
          phoneNo: doctor.phoneNo
        }));
        
        setAvailableDoctors(mappedDoctors);

        // Hasta randevularını database'den getir
        const patientAppointments = await appointmentAPI.getPatientAppointments(user.id);
        
        // Frontend için randevu bilgilerini düzenle
        const mappedAppointments = patientAppointments.map(appointment => ({
          id: appointment.appointmentId,
          patientId: appointment.patient.userId,
          patientName: `${appointment.patient.name} ${appointment.patient.surname}`,
          doctorId: appointment.doctor.userId,
          doctorName: `${appointment.doctor.name} ${appointment.doctor.surname}`,
          specialty: appointment.doctor.specialty,
          date: appointment.appointmentDateTime.split('T')[0], // LocalDateTime'den tarih
          time: appointment.appointmentDateTime.split('T')[1].substring(0, 5), // LocalDateTime'den saat
          notes: appointment.doctorNote || '',
          status: appointment.status,
          createdAt: appointment.appointmentDateTime
        }));
        
        setAppointments(mappedAppointments);
        
      } catch (error) {
        setError('Veriler yüklenirken hata oluştu: ' + error.message);
        console.error('Data yükleme hatası:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [user.id]);

  // Uzmanlık alanı seçildiğinde doktorları filtrele
  useEffect(() => {
    if (appointmentForm.specialty) {
      const filtered = availableDoctors.filter(doctor => doctor.specialty === appointmentForm.specialty);
      setFilteredDoctors(filtered);
    } else {
      setFilteredDoctors([]);
    }
    // Uzmanlık değiştiğinde doktor seçimini sıfırla
    setAppointmentForm(prev => ({ ...prev, doctorId: '' }));
  }, [appointmentForm.specialty, availableDoctors]);

  const handleAppointmentSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!appointmentForm.specialty || !appointmentForm.doctorId || !appointmentForm.date || !appointmentForm.time) {
        throw new Error('Lütfen tüm alanları doldurun');
      }
      
      // LocalDateTime formatında birleştir
      const appointmentDateTime = `${appointmentForm.date}T${appointmentForm.time}:00`;
      
      // Database'e randevu oluştur
      const newAppointment = await appointmentAPI.createAppointment(
        appointmentForm.doctorId,
        user.id,
        appointmentDateTime,
        appointmentForm.notes
      );

      // Randevu listesini yenile
      const patientAppointments = await appointmentAPI.getPatientAppointments(user.id);
      const mappedAppointments = patientAppointments.map(appointment => ({
        id: appointment.appointmentId,
        patientId: appointment.patient.userId,
        patientName: `${appointment.patient.name} ${appointment.patient.surname}`,
        doctorId: appointment.doctor.userId,
        doctorName: `${appointment.doctor.name} ${appointment.doctor.surname}`,
        specialty: appointment.doctor.specialty,
        date: appointment.appointmentDateTime.split('T')[0],
        time: appointment.appointmentDateTime.split('T')[1].substring(0, 5),
        notes: appointment.doctorNote || '',
        status: appointment.status,
        createdAt: appointment.appointmentDateTime
      }));
      
      setAppointments(mappedAppointments);

      // Formu sıfırla
      setAppointmentForm({
        specialty: '',
        doctorId: '',
        date: '',
        time: '',
        notes: ''
      });

      alert('Randevu başarıyla oluşturuldu!');
      setActiveTab('appointments');

    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setAppointmentForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const cancelAppointment = async (appointmentId) => {
    if (!window.confirm('Randevuyu iptal etmek istediğinizden emin misiniz?')) {
      return;
    }

    try {
      setLoading(true);
      await appointmentAPI.cancelAppointment(appointmentId);
      
      // Randevu listesini yenile
      const patientAppointments = await appointmentAPI.getPatientAppointments(user.id);
      const mappedAppointments = patientAppointments.map(appointment => ({
        id: appointment.appointmentId,
        patientId: appointment.patient.userId,
        patientName: `${appointment.patient.name} ${appointment.patient.surname}`,
        doctorId: appointment.doctor.userId,
        doctorName: `${appointment.doctor.name} ${appointment.doctor.surname}`,
        specialty: appointment.doctor.specialty,
        date: appointment.appointmentDateTime.split('T')[0],
        time: appointment.appointmentDateTime.split('T')[1].substring(0, 5),
        notes: appointment.doctorNote || '',
        status: appointment.status,
        createdAt: appointment.appointmentDateTime
      }));
      
      setAppointments(mappedAppointments);
      alert('Randevu başarıyla iptal edildi.');
    } catch (error) {
      setError('Randevu iptal edilirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const getStatusText = (status) => {
    const statusMap = {
      'PENDING': 'Beklemede',
      'CONFIRMED': 'Onaylandı',
      'CANCELLED': 'İptal Edildi'
    };
    return statusMap[status] || status;
  };

  const getSpecialtyLabel = (value) => {
    const specialty = specialties.find(s => s.value === value);
    return specialty ? specialty.label : value;
  };

  const renderAppointments = () => {
    if (appointments.length === 0) {
      return (
        <div className="no-data">
          <p>Henüz randevunuz bulunmuyor.</p>
          <button 
            onClick={() => setActiveTab('new-appointment')}
            className="btn btn-primary"
          >
            İlk Randevunu Oluştur
          </button>
        </div>
      );
    }

    return (
      <div className="appointments-list">
        {appointments.map(appointment => (
          <div key={appointment.id} className="appointment-card medical-card">
            <div className="appointment-header">
              <h4>Dr. {appointment.doctorName}</h4>
              <span className={`status ${appointment.status.toLowerCase()}`}>
                {getStatusText(appointment.status)}
              </span>
            </div>
            <div className="appointment-details">
              <p><strong>Uzmanlık:</strong> {getSpecialtyLabel(appointment.specialty)}</p>
              <p><strong>Tarih:</strong> {new Date(appointment.date).toLocaleDateString('tr-TR')}</p>
              <p><strong>Saat:</strong> {appointment.time}</p>
              {appointment.notes && <p><strong>Notlar:</strong> {appointment.notes}</p>}
            </div>
            {appointment.status === 'PENDING' && (
              <div className="appointment-actions">
                <button 
                  onClick={() => cancelAppointment(appointment.id)}
                  className="btn btn-danger"
                  disabled={loading}
                >
                  İptal Et
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    );
  };

  const renderNewAppointment = () => (
    <div className="new-appointment-section">
      <h3>Yeni Randevu Oluştur</h3>
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleAppointmentSubmit} className="appointment-form">
        <div className="form-group">
          <label htmlFor="specialty">Uzmanlık Alanı</label>
          <select
            id="specialty"
            name="specialty"
            value={appointmentForm.specialty}
            onChange={handleFormChange}
            required
          >
            <option value="">Uzmanlık alanı seçin</option>
            {specialties.map(specialty => (
              <option key={specialty.value} value={specialty.value}>
                {specialty.label}
              </option>
            ))}
          </select>
        </div>

        {appointmentForm.specialty && (
          <div className="form-group">
            <label htmlFor="doctorId">Doktor</label>
            <select
              id="doctorId"
              name="doctorId"
              value={appointmentForm.doctorId}
              onChange={handleFormChange}
              required
            >
              <option value="">Doktor seçin</option>
              {filteredDoctors.map(doctor => (
                <option key={doctor.id} value={doctor.id}>
                  Dr. {doctor.name}
                </option>
              ))}
            </select>
          </div>
        )}

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="date">Tarih</label>
            <input
              type="date"
              id="date"
              name="date"
              value={appointmentForm.date}
              onChange={handleFormChange}
              min={new Date().toISOString().split('T')[0]}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="time">Saat</label>
            <input
              type="time"
              id="time"
              name="time"
              value={appointmentForm.time}
              onChange={handleFormChange}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="notes">Notlar (Opsiyonel)</label>
          <textarea
            id="notes"
            name="notes"
            value={appointmentForm.notes}
            onChange={handleFormChange}
            placeholder="Randevu ile ilgili notlarınızı yazın..."
            rows={3}
          />
        </div>

        <div className="form-actions">
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? 'Randevu Oluşturuluyor...' : 'Randevu Oluştur'}
          </button>
          <button 
            type="button" 
            onClick={() => setActiveTab('appointments')}
            className="btn btn-secondary"
          >
            İptal
          </button>
        </div>
      </form>
    </div>
  );

  return (
    <div className="patient-dashboard">
      <div className="dashboard-header">
        <h2>Hasta Paneli</h2>
        <div className="user-info">
          <span>Hoş geldin, {user.name}!</span>
          <button onClick={onLogout} className="btn btn-logout">
            Çıkış Yap
          </button>
        </div>
      </div>

      <div className="dashboard-nav">
        <button
          className={`nav-btn ${activeTab === 'appointments' ? 'active' : ''}`}
          onClick={() => setActiveTab('appointments')}
        >
          Randevularım
        </button>
        <button
          className={`nav-btn ${activeTab === 'new-appointment' ? 'active' : ''}`}
          onClick={() => setActiveTab('new-appointment')}
        >
          Yeni Randevu
        </button>
      </div>

      <div className="dashboard-content">
        {loading && <div className="loading">Yükleniyor...</div>}
        {activeTab === 'appointments' && renderAppointments()}
        {activeTab === 'new-appointment' && renderNewAppointment()}
      </div>
    </div>
  );
};

export default PatientDashboard;