import React, { useState, useEffect } from 'react';
import { userAPI } from '../services/api';
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

  // Kullanıcı bazlı verileri database'den yükle
  useEffect(() => {
    const loadDoctors = async () => {
      try {
        setLoading(true);
        // Database'den tüm doktorları getir
        const doctors = await userAPI.getAllDoctors();
        
        // Frontend için doktor bilgilerini düzenle
        const mappedDoctors = doctors.map(doctor => ({
          id: doctor.userId,
          name: `${doctor.name} ${doctor.surname}`,
          email: doctor.email,
          role: 'doktor',
          specialty: doctor.specialty, // Database'den gelen gerçek specialty
          availability: doctor.availability,
          birthDate: doctor.birthDate,
          phoneNo: doctor.phoneNo
        }));
        
        setAvailableDoctors(mappedDoctors);
        
        // Kullanıcıya özel randevuları localStorage'dan yükle (geçici olarak)
        // TODO: İleride randevu API'si eklendiğinde burası da database'den gelecek
    const userAppointmentsKey = `appointments_patient_${user.id}`;
    const savedAppointments = localStorage.getItem(userAppointmentsKey);
    
    if (savedAppointments) {
      setAppointments(JSON.parse(savedAppointments));
    } else {
      setAppointments([]);
    }
      } catch (error) {
        setError('Doktor listesi yüklenirken hata oluştu: ' + error.message);
        console.error('Doktor yükleme hatası:', error);
      } finally {
        setLoading(false);
      }
    };

    loadDoctors();
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

  // Randevuları localStorage'a kaydet (geçici - ileride database'e kaydedilecek)
  const saveAppointments = (newAppointments) => {
    const userAppointmentsKey = `appointments_patient_${user.id}`;
    localStorage.setItem(userAppointmentsKey, JSON.stringify(newAppointments));
    setAppointments(newAppointments);
  };

  const handleAppointmentSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      if (!appointmentForm.specialty || !appointmentForm.doctorId || !appointmentForm.date || !appointmentForm.time) {
        throw new Error('Lütfen tüm alanları doldurun');
      }
      
      const selectedDoctor = availableDoctors.find(doc => doc.id.toString() === appointmentForm.doctorId);
      
      const newAppointment = {
        id: Date.now(),
        patientId: user.id,
        patientName: user.name,
        doctorId: parseInt(appointmentForm.doctorId),
        doctorName: selectedDoctor?.name || 'Bilinmeyen Doktor',
        specialty: appointmentForm.specialty,
        date: appointmentForm.date,
        time: appointmentForm.time,
        notes: appointmentForm.notes,
        status: 'PENDING',
        createdAt: new Date().toISOString()
      };

      // Randevuyu hasta listesine ekle
      const updatedAppointments = [...appointments, newAppointment];
      saveAppointments(updatedAppointments);

      // Doktor tarafına da randevuyu ekle (geçici localStorage)
      const doctorAppointmentsKey = `appointments_doctor_${appointmentForm.doctorId}`;
      const doctorAppointments = JSON.parse(localStorage.getItem(doctorAppointmentsKey) || '[]');
      doctorAppointments.push(newAppointment);
      localStorage.setItem(doctorAppointmentsKey, JSON.stringify(doctorAppointments));

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
    }
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setAppointmentForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const getStatusText = (status) => {
    const statusMap = {
      'PENDING': 'Beklemede',
      'CONFIRMED': 'Onaylandı',
      'COMPLETED': 'Tamamlandı',
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
          </div>
        ))}
      </div>
    );
  };

  const renderNewAppointment = () => {
    return (
      <div className="new-appointment-form medical-card">
        <h3>Yeni Randevu Oluştur</h3>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleAppointmentSubmit}>
          <div className="form-group">
            <label htmlFor="specialty">Uzmanlık Alanı</label>
            <select
              id="specialty"
              name="specialty"
              value={appointmentForm.specialty}
              onChange={handleFormChange}
              required
            >
              <option value="">Uzmanlık alanı seçiniz...</option>
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
                <option value="">Doktor seçiniz...</option>
                {filteredDoctors.map(doctor => (
                  <option key={doctor.id} value={doctor.id}>
                    Dr. {doctor.name}
                    {doctor.availability ? ' ✅' : ' ❌'}
                  </option>
                ))}
              </select>
              {filteredDoctors.length === 0 && appointmentForm.specialty && (
                <p className="no-doctors-info">Bu uzmanlık alanında müsait doktor bulunmuyor.</p>
              )}
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
              <select
                id="time"
                name="time"
                value={appointmentForm.time}
                onChange={handleFormChange}
                required
              >
                <option value="">Saat seçiniz...</option>
                <option value="09:00">09:00</option>
                <option value="09:30">09:30</option>
                <option value="10:00">10:00</option>
                <option value="10:30">10:30</option>
                <option value="11:00">11:00</option>
                <option value="11:30">11:30</option>
                <option value="14:00">14:00</option>
                <option value="14:30">14:30</option>
                <option value="15:00">15:00</option>
                <option value="15:30">15:30</option>
                <option value="16:00">16:00</option>
                <option value="16:30">16:30</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="notes">Notlar (Opsiyonel)</label>
            <textarea
              id="notes"
              name="notes"
              value={appointmentForm.notes}
              onChange={handleFormChange}
              placeholder="Randevu ile ilgili notlarınızı girebilirsiniz..."
              rows="3"
            />
          </div>

          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Randevu Oluşturuluyor...' : 'Randevu Oluştur'}
          </button>
        </form>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="patient-dashboard">
        <div className="loading-message">Veriler yükleniyor...</div>
      </div>
    );
  }

  return (
    <div className="patient-dashboard">
      <div className="dashboard-header medical-card">
        <h2>Hasta Paneli</h2>
        <p>Hoş geldiniz, <strong>{user.name}</strong></p>
        <div className="patient-id">Hasta ID: #{user.id}</div>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={`tab ${activeTab === 'appointments' ? 'active' : ''}`}
          onClick={() => setActiveTab('appointments')}
        >
          Randevu Geçmişi ({appointments.length})
        </button>
        <button 
          className={`tab ${activeTab === 'new-appointment' ? 'active' : ''}`}
          onClick={() => setActiveTab('new-appointment')}
        >
          Yeni Randevu
        </button>
      </div>

      <div className="dashboard-content">
        {activeTab === 'appointments' && renderAppointments()}
        {activeTab === 'new-appointment' && renderNewAppointment()}
      </div>
    </div>
  );
};

export default PatientDashboard;