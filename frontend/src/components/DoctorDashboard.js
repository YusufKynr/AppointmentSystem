import React, { useState, useEffect } from 'react';
import './DoctorDashboard.css';

const DoctorDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('PENDING');
  const [appointments, setAppointments] = useState([]);
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [doctorNotes, setDoctorNotes] = useState('');
  const [loading, setLoading] = useState(false);

  // Doktora özel randevuları localStorage'dan yükle
  useEffect(() => {
    const doctorAppointmentsKey = `appointments_doctor_${user.id}`;
    const savedAppointments = localStorage.getItem(doctorAppointmentsKey);
    
    if (savedAppointments) {
      setAppointments(JSON.parse(savedAppointments));
    } else {
      // Yeni doktor hesapları için boş liste
      setAppointments([]);
    }
  }, [user.id]);

  // Randevuları localStorage'a kaydet
  const saveAppointments = (newAppointments) => {
    const doctorAppointmentsKey = `appointments_doctor_${user.id}`;
    localStorage.setItem(doctorAppointmentsKey, JSON.stringify(newAppointments));
    setAppointments(newAppointments);
  };

  const handleAppointmentAction = async (appointmentId, action, notes = '') => {
    setLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const updatedAppointments = appointments.map(apt => 
        apt.id === appointmentId 
          ? { ...apt, status: action, doctorNotes: notes }
          : apt
      );

      saveAppointments(updatedAppointments);

      // Hasta için de randevu durumunu güncelle
      const appointment = appointments.find(apt => apt.id === appointmentId);
      if (appointment && appointment.patientId) {
        const patientAppointmentsKey = `appointments_patient_${appointment.patientId}`;
        const patientAppointments = JSON.parse(localStorage.getItem(patientAppointmentsKey) || '[]');
        const updatedPatientAppointments = patientAppointments.map(apt =>
          apt.id === appointmentId
            ? { ...apt, status: action, doctorNotes: notes }
            : apt
        );
        localStorage.setItem(patientAppointmentsKey, JSON.stringify(updatedPatientAppointments));
      }

      if (selectedAppointment && selectedAppointment.id === appointmentId) {
        setSelectedAppointment({ ...selectedAppointment, status: action, doctorNotes: notes });
      }

      setDoctorNotes('');
      alert(`Randevu ${action === 'CONFIRMED' ? 'onaylandı' : 'iptal edildi'}`);
    } catch (error) {
      alert('İşlem sırasında hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  const filterAppointments = (status) => {
    return appointments.filter(apt => apt.status === status);
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'CONFIRMED': return 'status-approved';
      case 'CANCELLED': return 'status-rejected';
      default: return 'status-pending';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'CONFIRMED': return 'Onaylandı';
      case 'CANCELLED': return 'İptal Edildi';
      default: return 'Beklemede';
    }
  };

  const openAppointmentModal = (appointment) => {
    setSelectedAppointment(appointment);
    setDoctorNotes(appointment.doctorNotes || '');
  };

  const closeAppointmentModal = () => {
    setSelectedAppointment(null);
    setDoctorNotes('');
  };

  return (
    <div className="doctor-dashboard">
      <div className="dashboard-header medical-card">
        <h2>Doktor Paneli</h2>
        <p>Hoş geldiniz, <strong>{user.name}</strong></p>
        <div className="doctor-id">Doktor ID: #{user.id}</div>
      </div>

      <div className="dashboard-stats">
        <div className="stat-card medical-card">
          <h3>{filterAppointments('PENDING').length}</h3>
          <p>Bekleyen Randevular</p>
        </div>
        <div className="stat-card medical-card">
          <h3>{filterAppointments('CONFIRMED').length}</h3>
          <p>Onaylanan Randevular</p>
        </div>
        <div className="stat-card medical-card">
          <h3>{filterAppointments('CANCELLED').length}</h3>
          <p>İptal Edilen Randevular</p>
        </div>
        <div className="stat-card medical-card">
          <h3>{appointments.length}</h3>
          <p>Toplam Randevu</p>
        </div>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={`tab ${activeTab === 'PENDING' ? 'active' : ''}`}
          onClick={() => setActiveTab('PENDING')}
        >
          Bekleyen ({filterAppointments('PENDING').length})
        </button>
        <button 
          className={`tab ${activeTab === 'CONFIRMED' ? 'active' : ''}`}
          onClick={() => setActiveTab('CONFIRMED')}
        >
          Onaylanan ({filterAppointments('CONFIRMED').length})
        </button>
        <button 
          className={`tab ${activeTab === 'CANCELLED' ? 'active' : ''}`}
          onClick={() => setActiveTab('CANCELLED')}
        >
          İptal Edilen ({filterAppointments('CANCELLED').length})
        </button>
      </div>

      <div className="dashboard-content">
        <div className="appointments-list">
          {activeTab === 'PENDING' && (
            <div className="appointments-section">
              <h3>Onay Bekleyen Randevular</h3>
              {filterAppointments('PENDING').length === 0 ? (
                <div className="empty-state">
                  <p>Bekleyen randevu bulunmuyor.</p>
                  <p>Hastalar yeni randevu talep ettiğinde burada görünecek.</p>
                </div>
              ) : (
                <div className="appointments-grid">
                  {filterAppointments('PENDING').map(appointment => (
                    <div key={appointment.id} className={`appointment-card medical-card ${getStatusClass(appointment.status)}`}>
                      <div className="appointment-header">
                        <h4>{appointment.patientName}</h4>
                        <span className={`status-badge ${getStatusClass(appointment.status)}`}>
                          {getStatusText(appointment.status)}
                        </span>
                      </div>
                      <div className="appointment-details">
                        <p><strong>Tarih:</strong> {new Date(appointment.date).toLocaleDateString('tr-TR')}</p>
                        <p><strong>Saat:</strong> {appointment.time}</p>
                        <p><strong>Telefon:</strong> {appointment.patientPhone}</p>
                        <p><strong>Yaş:</strong> {appointment.patientAge}</p>
                        {appointment.notes && (
                          <p><strong>Hasta Notu:</strong> {appointment.notes}</p>
                        )}
                      </div>
                      <div className="appointment-actions">
                        <button 
                          onClick={() => openAppointmentModal(appointment)}
                          className="detail-btn btn"
                        >
                          Detay
                        </button>
                        <button 
                          onClick={() => handleAppointmentAction(appointment.id, 'CONFIRMED')}
                          className="approve-btn btn btn-success"
                          disabled={loading}
                        >
                          Onayla
                        </button>
                        <button 
                          onClick={() => handleAppointmentAction(appointment.id, 'CANCELLED')}
                          className="reject-btn btn btn-danger"
                          disabled={loading}
                        >
                          İptal
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'CONFIRMED' && (
            <div className="appointments-section">
              <h3>Onaylanan Randevular</h3>
              {filterAppointments('CONFIRMED').length === 0 ? (
                <div className="empty-state">
                  <p>Onaylanan randevu bulunmuyor.</p>
                </div>
              ) : (
                <div className="appointments-grid">
                  {filterAppointments('CONFIRMED').map(appointment => (
                    <div key={appointment.id} className={`appointment-card medical-card ${getStatusClass(appointment.status)}`}>
                      <div className="appointment-header">
                        <h4>{appointment.patientName}</h4>
                        <span className={`status-badge ${getStatusClass(appointment.status)}`}>
                          {getStatusText(appointment.status)}
                        </span>
                      </div>
                      <div className="appointment-details">
                        <p><strong>Tarih:</strong> {new Date(appointment.date).toLocaleDateString('tr-TR')}</p>
                        <p><strong>Saat:</strong> {appointment.time}</p>
                        <p><strong>Telefon:</strong> {appointment.patientPhone}</p>
                        {appointment.doctorNotes && (
                          <div className="doctor-notes-preview">
                            <strong>Doktor Notları:</strong>
                            <p>{appointment.doctorNotes}</p>
                          </div>
                        )}
                      </div>
                      <div className="appointment-actions">
                        <button 
                          onClick={() => openAppointmentModal(appointment)}
                          className="detail-btn btn"
                        >
                          Detay/Not Ekle
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'CANCELLED' && (
            <div className="appointments-section">
              <h3>İptal Edilen Randevular</h3>
              {filterAppointments('CANCELLED').length === 0 ? (
                <div className="empty-state">
                  <p>İptal edilen randevu bulunmuyor.</p>
                </div>
              ) : (
                <div className="appointments-grid">
                  {filterAppointments('CANCELLED').map(appointment => (
                    <div key={appointment.id} className={`appointment-card medical-card ${getStatusClass(appointment.status)}`}>
                      <div className="appointment-header">
                        <h4>{appointment.patientName}</h4>
                        <span className={`status-badge ${getStatusClass(appointment.status)}`}>
                          {getStatusText(appointment.status)}
                        </span>
                      </div>
                      <div className="appointment-details">
                        <p><strong>Tarih:</strong> {new Date(appointment.date).toLocaleDateString('tr-TR')}</p>
                        <p><strong>Saat:</strong> {appointment.time}</p>
                        {appointment.doctorNotes && (
                          <div className="doctor-notes-preview">
                            <strong>İptal Sebebi:</strong>
                            <p>{appointment.doctorNotes}</p>
                          </div>
                        )}
                      </div>
                      <div className="appointment-actions">
                        <button 
                          onClick={() => openAppointmentModal(appointment)}
                          className="detail-btn btn"
                        >
                          Detay
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Randevu Detay Modal */}
      {selectedAppointment && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Randevu Detayları</h3>
              <button onClick={closeAppointmentModal} className="close-btn">✕</button>
            </div>
            <div className="modal-body">
              <div className="patient-info">
                <h4>Hasta Bilgileri</h4>
                <p><strong>Ad Soyad:</strong> {selectedAppointment.patientName}</p>
                <p><strong>Telefon:</strong> {selectedAppointment.patientPhone}</p>
                <p><strong>Yaş:</strong> {selectedAppointment.patientAge}</p>
                <p><strong>Randevu Tarihi:</strong> {new Date(selectedAppointment.date).toLocaleDateString('tr-TR')}</p>
                <p><strong>Randevu Saati:</strong> {selectedAppointment.time}</p>
                {selectedAppointment.notes && (
                  <p><strong>Hasta Notu:</strong> {selectedAppointment.notes}</p>
                )}
              </div>
              <div className="doctor-notes-section">
                <h4>Doktor Notları</h4>
                <textarea
                  value={doctorNotes}
                  onChange={(e) => setDoctorNotes(e.target.value)}
                  placeholder="Hasta ile ilgili notlarınızı yazın..."
                  rows="4"
                  className="notes-textarea"
                />
              </div>
            </div>
            <div className="modal-actions">
              {selectedAppointment.status === 'PENDING' && (
                <>
                  <button 
                    onClick={() => handleAppointmentAction(selectedAppointment.id, 'CONFIRMED', doctorNotes)}
                    className="approve-btn btn btn-success"
                    disabled={loading}
                  >
                    Onayla ve Not Kaydet
                  </button>
                  <button 
                    onClick={() => handleAppointmentAction(selectedAppointment.id, 'CANCELLED', doctorNotes)}
                    className="reject-btn btn btn-danger"
                    disabled={loading}
                  >
                    İptal ve Not Kaydet
                  </button>
                </>
              )}
              {selectedAppointment.status === 'CONFIRMED' && (
                <button 
                  onClick={() => handleAppointmentAction(selectedAppointment.id, 'CONFIRMED', doctorNotes)}
                  className="save-btn btn btn-primary"
                  disabled={loading}
                >
                  Notları Kaydet
                </button>
              )}
              <button onClick={closeAppointmentModal} className="cancel-btn btn">
                İptal
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DoctorDashboard; 