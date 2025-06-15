import React, { useState, useEffect } from 'react';
import { appointmentAPI } from '../services/api';
import './DoctorDashboard.css';

const DoctorDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('PENDING');
  const [appointments, setAppointments] = useState([]);
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [doctorNotes, setDoctorNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Doktora özel randevuları database'den yükle
  useEffect(() => {
    const loadAppointments = async () => {
      try {
        setLoading(true);
        setError('');
        
        const doctorAppointments = await appointmentAPI.getDoctorAppointments(user.id);
        
        // Frontend için randevu bilgilerini düzenle
        const mappedAppointments = doctorAppointments.map(appointment => ({
          id: appointment.appointmentId,
          patientId: appointment.patient.userId,
          patientName: `${appointment.patient.name} ${appointment.patient.surname}`,
          patientPhone: appointment.patient.phoneNo,
          patientAge: calculateAge(appointment.patient.birthDate),
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
      } catch (error) {
        setError('Randevular yüklenirken hata oluştu: ' + error.message);
        console.error('Randevu yükleme hatası:', error);
      } finally {
        setLoading(false);
      }
    };

    loadAppointments();
  }, [user.id]);

  // Yaş hesaplama fonksiyonu
  const calculateAge = (birthDate) => {
    if (!birthDate) return 'Bilinmiyor';
    
    const birth = new Date(birthDate);
    const today = new Date();
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    
    return age;
  };

  // Randevu listesini yenile
  const refreshAppointments = async () => {
    try {
      const doctorAppointments = await appointmentAPI.getDoctorAppointments(user.id);
      const mappedAppointments = doctorAppointments.map(appointment => ({
        id: appointment.appointmentId,
        patientId: appointment.patient.userId,
        patientName: `${appointment.patient.name} ${appointment.patient.surname}`,
        patientPhone: appointment.patient.phoneNo,
        patientAge: calculateAge(appointment.patient.birthDate),
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
    } catch (error) {
      setError('Randevular yenilenirken hata oluştu: ' + error.message);
    }
  };

  const handleAppointmentAction = async (appointmentId, action, notes = '') => {
    setLoading(true);
    setError('');
    
    try {
      if (action === 'CONFIRMED') {
        await appointmentAPI.approveAppointment(appointmentId);
      } else if (action === 'CANCELLED') {
        await appointmentAPI.rejectAppointment(appointmentId);
      }

      // Not ekle
      if (notes && notes.trim() !== '') {
        await appointmentAPI.setAppointmentNote(appointmentId, notes);
      }

      // Randevu listesini yenile
      await refreshAppointments();

      // Seçili randevu modal'ını kapat
      if (selectedAppointment && selectedAppointment.id === appointmentId) {
        setSelectedAppointment(null);
      }

      setDoctorNotes('');
      alert(`Randevu ${action === 'CONFIRMED' ? 'onaylandı' : 'iptal edildi'}`);
    } catch (error) {
      setError('İşlem sırasında hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddNote = async (appointmentId) => {
    if (!doctorNotes.trim()) {
      alert('Lütfen bir not girin.');
      return;
    }

    setLoading(true);
    try {
      await appointmentAPI.setAppointmentNote(appointmentId, doctorNotes);
      await refreshAppointments();
      setDoctorNotes('');
      setSelectedAppointment(null);
      alert('Randevu notu başarıyla eklendi.');
    } catch (error) {
      setError('Not eklenirken hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const filterAppointments = (status) => {
    return appointments.filter(apt => apt.status === status);
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
    const specialtyMap = {
      'Dermatology': 'Dermatoloji',
      'Cardiology': 'Kardiyoloji',
      'Eye': 'Göz Hastalıkları',
      'General_Surgery': 'Genel Cerrahi'
    };
    return specialtyMap[value] || value;
  };

  const renderAppointmentCard = (appointment) => (
    <div key={appointment.id} className="appointment-card">
      <div className="appointment-header">
        <h4>{appointment.patientName}</h4>
        <span className={`status ${appointment.status.toLowerCase()}`}>
          {getStatusText(appointment.status)}
        </span>
      </div>
      <div className="appointment-details">
        <p><strong>Tarih:</strong> {new Date(appointment.date).toLocaleDateString('tr-TR')}</p>
        <p><strong>Saat:</strong> {appointment.time}</p>
        <p><strong>Telefon:</strong> {appointment.patientPhone}</p>
        <p><strong>Yaş:</strong> {appointment.patientAge}</p>
        {appointment.notes && (
          <div className="doctor-notes-preview">
            <strong>Doktor Notu:</strong>
            <p>{appointment.notes}</p>
          </div>
        )}
      </div>
      <div className="appointment-actions">
        <button
          onClick={() => setSelectedAppointment(appointment)}
          className="btn btn-info"
        >
          Detay
        </button>
        {appointment.status === 'PENDING' && (
          <>
            <button
              onClick={() => handleAppointmentAction(appointment.id, 'CONFIRMED')}
              className="btn btn-success"
              disabled={loading}
            >
              Onayla
            </button>
            <button
              onClick={() => handleAppointmentAction(appointment.id, 'CANCELLED')}
              className="btn btn-danger"
              disabled={loading}
            >
              Reddet
            </button>
          </>
        )}
      </div>
    </div>
  );

  const renderModal = () => {
    if (!selectedAppointment) return null;

    return (
      <div className="modal-overlay" onClick={() => setSelectedAppointment(null)}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h3>Randevu Detayları</h3>
            <button
              onClick={() => setSelectedAppointment(null)}
              className="close-btn"
            >
              ×
            </button>
          </div>
          <div className="modal-body">
            <div className="patient-info">
              <h4>Hasta Bilgileri</h4>
              <p><strong>Ad Soyad:</strong> {selectedAppointment.patientName}</p>
              <p><strong>Telefon:</strong> {selectedAppointment.patientPhone}</p>
              <p><strong>Yaş:</strong> {selectedAppointment.patientAge}</p>
            </div>
            <div className="appointment-info">
              <h4>Randevu Bilgileri</h4>
              <p><strong>Tarih:</strong> {new Date(selectedAppointment.date).toLocaleDateString('tr-TR')}</p>
              <p><strong>Saat:</strong> {selectedAppointment.time}</p>
              <p><strong>Durum:</strong> {getStatusText(selectedAppointment.status)}</p>
            </div>
            {selectedAppointment.notes && (
              <div className="existing-notes">
                <h4>Mevcut Doktor Notu</h4>
                <p>{selectedAppointment.notes}</p>
              </div>
            )}
            <div className="doctor-notes-section">
              <h4>Doktor Notu Ekle</h4>
              <textarea
                value={doctorNotes}
                onChange={(e) => setDoctorNotes(e.target.value)}
                placeholder="Randevu ile ilgili notlarınızı yazın..."
                rows={4}
                className="notes-textarea"
              />
            </div>
          </div>
          <div className="modal-actions">
            <button
              onClick={() => handleAddNote(selectedAppointment.id)}
              className="btn btn-success"
              disabled={loading || !doctorNotes.trim()}
            >
              {loading ? 'Kaydediliyor...' : 'Notu Kaydet'}
            </button>
            <button
              onClick={() => setSelectedAppointment(null)}
              className="btn btn-secondary"
            >
              İptal
            </button>
          </div>
        </div>
      </div>
    );
  };

  // İstatistik hesaplama
  const getStats = () => {
    return {
      pending: filterAppointments('PENDING').length,
      confirmed: filterAppointments('CONFIRMED').length,
      cancelled: filterAppointments('CANCELLED').length,
      total: appointments.length
    };
  };

  const stats = getStats();

  return (
    <div className="doctor-dashboard">
      <div className="dashboard-header">
        <h2>Doktor Paneli</h2>
        <div className="user-info">
          <span>Hoş geldin, Dr. {user.name}!</span>
          <button onClick={onLogout} className="btn-logout">
            Çıkış Yap
          </button>
        </div>
      </div>

      {/* İstatistik Kartları */}
      <div className="dashboard-stats">
        <div className="stat-card">
          <h3>{stats.total}</h3>
          <p>Toplam Randevu</p>
        </div>
        <div className="stat-card">
          <h3>{stats.pending}</h3>
          <p>Bekleyen</p>
        </div>
        <div className="stat-card">
          <h3>{stats.confirmed}</h3>
          <p>Onaylanan</p>
        </div>
        <div className="stat-card">
          <h3>{stats.cancelled}</h3>
          <p>İptal Edilen</p>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="dashboard-nav">
        <button
          className={`nav-btn ${activeTab === 'PENDING' ? 'active' : ''}`}
          onClick={() => setActiveTab('PENDING')}
        >
          Bekleyenler ({stats.pending})
        </button>
        <button
          className={`nav-btn ${activeTab === 'CONFIRMED' ? 'active' : ''}`}
          onClick={() => setActiveTab('CONFIRMED')}
        >
          Onaylananlar ({stats.confirmed})
        </button>
        <button
          className={`nav-btn ${activeTab === 'CANCELLED' ? 'active' : ''}`}
          onClick={() => setActiveTab('CANCELLED')}
        >
          İptal Edilenler ({stats.cancelled})
        </button>
      </div>

      <div className="dashboard-content">
        {loading && <div className="loading">Yükleniyor...</div>}
        
        {activeTab === 'PENDING' && (
          <div className="appointments-section">
            <h3>Onay Bekleyen Randevular</h3>
            {filterAppointments('PENDING').length === 0 ? (
              <div className="no-data">
                <p>Bekleyen randevu bulunmuyor.</p>
              </div>
            ) : (
              <div className="appointments-list">
                {filterAppointments('PENDING').map(renderAppointmentCard)}
              </div>
            )}
          </div>
        )}

        {activeTab === 'CONFIRMED' && (
          <div className="appointments-section">
            <h3>Onaylanmış Randevular</h3>
            {filterAppointments('CONFIRMED').length === 0 ? (
              <div className="no-data">
                <p>Onaylanmış randevu bulunmuyor.</p>
              </div>
            ) : (
              <div className="appointments-list">
                {filterAppointments('CONFIRMED').map(renderAppointmentCard)}
              </div>
            )}
          </div>
        )}

        {activeTab === 'CANCELLED' && (
          <div className="appointments-section">
            <h3>İptal Edilmiş Randevular</h3>
            {filterAppointments('CANCELLED').length === 0 ? (
              <div className="no-data">
                <p>İptal edilmiş randevu bulunmuyor.</p>
              </div>
            ) : (
              <div className="appointments-list">
                {filterAppointments('CANCELLED').map(renderAppointmentCard)}
              </div>
            )}
          </div>
        )}
      </div>

      {renderModal()}
    </div>
  );
};

export default DoctorDashboard; 