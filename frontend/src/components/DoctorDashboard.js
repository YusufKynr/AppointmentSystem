import React, { useState, useEffect } from 'react';
import './DoctorDashboard.css';

const DoctorDashboard = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState('pending');
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
      alert(`Randevu ${action === 'onaylandı' ? 'onaylandı' : 'reddedildi'}`);
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
      case 'onaylandı': return 'status-approved';
      case 'reddedildi': return 'status-rejected';
      default: return 'status-pending';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'onaylandı': return 'Onaylandı';
      case 'reddedildi': return 'Reddedildi';
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
          <h3>{filterAppointments('beklemede').length}</h3>
          <p>Bekleyen Randevular</p>
        </div>
        <div className="stat-card medical-card">
          <h3>{filterAppointments('onaylandı').length}</h3>
          <p>Onaylanan Randevular</p>
        </div>
        <div className="stat-card medical-card">
          <h3>{filterAppointments('reddedildi').length}</h3>
          <p>Reddedilen Randevular</p>
        </div>
        <div className="stat-card medical-card">
          <h3>{appointments.length}</h3>
          <p>Toplam Randevu</p>
        </div>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={`tab ${activeTab === 'pending' ? 'active' : ''}`}
          onClick={() => setActiveTab('pending')}
        >
          Bekleyen ({filterAppointments('beklemede').length})
        </button>
        <button 
          className={`tab ${activeTab === 'approved' ? 'active' : ''}`}
          onClick={() => setActiveTab('approved')}
        >
          Onaylanan ({filterAppointments('onaylandı').length})
        </button>
        <button 
          className={`tab ${activeTab === 'rejected' ? 'active' : ''}`}
          onClick={() => setActiveTab('rejected')}
        >
          Reddedilen ({filterAppointments('reddedildi').length})
        </button>
      </div>

      <div className="dashboard-content">
        <div className="appointments-list">
          {activeTab === 'pending' && (
            <div className="appointments-section">
              <h3>Onay Bekleyen Randevular</h3>
              {filterAppointments('beklemede').length === 0 ? (
                <div className="empty-state">
                  <p>Bekleyen randevu bulunmuyor.</p>
                  <p>Hastalar yeni randevu talep ettiğinde burada görünecek.</p>
                </div>
              ) : (
                <div className="appointments-grid">
                  {filterAppointments('beklemede').map(appointment => (
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
                          onClick={() => handleAppointmentAction(appointment.id, 'onaylandı')}
                          className="approve-btn btn btn-success"
                          disabled={loading}
                        >
                          Onayla
                        </button>
                        <button 
                          onClick={() => handleAppointmentAction(appointment.id, 'reddedildi')}
                          className="reject-btn btn btn-danger"
                          disabled={loading}
                        >
                          Reddet
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'approved' && (
            <div className="appointments-section">
              <h3>Onaylanan Randevular</h3>
              {filterAppointments('onaylandı').length === 0 ? (
                <div className="empty-state">
                  <p>Onaylanan randevu bulunmuyor.</p>
                </div>
              ) : (
                <div className="appointments-grid">
                  {filterAppointments('onaylandı').map(appointment => (
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

          {activeTab === 'rejected' && (
            <div className="appointments-section">
              <h3>Reddedilen Randevular</h3>
              {filterAppointments('reddedildi').length === 0 ? (
                <div className="empty-state">
                  <p>Reddedilen randevu bulunmuyor.</p>
                </div>
              ) : (
                <div className="appointments-grid">
                  {filterAppointments('reddedildi').map(appointment => (
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
                            <strong>Red Sebebi:</strong>
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
              {selectedAppointment.status === 'beklemede' && (
                <>
                  <button 
                    onClick={() => handleAppointmentAction(selectedAppointment.id, 'onaylandı', doctorNotes)}
                    className="approve-btn btn btn-success"
                    disabled={loading}
                  >
                    Onayla ve Not Kaydet
                  </button>
                  <button 
                    onClick={() => handleAppointmentAction(selectedAppointment.id, 'reddedildi', doctorNotes)}
                    className="reject-btn btn btn-danger"
                    disabled={loading}
                  >
                    Reddet ve Not Kaydet
                  </button>
                </>
              )}
              {selectedAppointment.status === 'onaylandı' && (
                <button 
                  onClick={() => handleAppointmentAction(selectedAppointment.id, 'onaylandı', doctorNotes)}
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