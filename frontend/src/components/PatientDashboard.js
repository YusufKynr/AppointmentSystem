import React, { useState, useEffect } from 'react';
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

  const specialties = [
    { value: 'Dermatology', label: 'Dermatoloji' },
    { value: 'Cardiology', label: 'Kardiyoloji' },
    { value: 'Eye', label: 'Göz Hastalıkları' },
    { value: 'General_Surgery', label: 'Genel Cerrahi' }
  ];

  // Kullanıcı bazlı verileri localStorage'dan yükle
  useEffect(() => {
    // Kayıt olan doktorları getir
    const registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]');
    const doctors = registeredUsers.filter(user => user.role === 'doktor');
    setAvailableDoctors(doctors);

    // Kullanıcıya özel randevuları localStorage'dan yükle
    const userAppointmentsKey = `appointments_patient_${user.id}`;
    const savedAppointments = localStorage.getItem(userAppointmentsKey);
    
    if (savedAppointments) {
      setAppointments(JSON.parse(savedAppointments));
    } else {
      // Yeni kullanıcı için boş randevu listesi
      setAppointments([]);
    }
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

  // Randevuları localStorage'a kaydet
  const saveAppointments = (newAppointments) => {
    const userAppointmentsKey = `appointments_patient_${user.id}`;
    localStorage.setItem(userAppointmentsKey, JSON.stringify(newAppointments));
    setAppointments(newAppointments);
  };

  const handleAppointmentSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const selectedDoctor = availableDoctors.find(d => d.id === parseInt(appointmentForm.doctorId));
      const selectedSpecialty = specialties.find(s => s.value === appointmentForm.specialty);
      
      const newAppointment = {
        id: Date.now(),
        patientId: user.id,
        patientName: user.name,
        doctorName: selectedDoctor.name,
        specialty: selectedSpecialty.label,
        specialtyValue: appointmentForm.specialty,
        date: appointmentForm.date,
        time: appointmentForm.time,
        status: 'PENDING',
        notes: appointmentForm.notes
      };

      const updatedAppointments = [...appointments, newAppointment];
      saveAppointments(updatedAppointments);

      // Doktor için de randevuyu kaydet
      const doctorAppointmentsKey = `appointments_doctor_${selectedDoctor.id}`;
      const doctorAppointments = JSON.parse(localStorage.getItem(doctorAppointmentsKey) || '[]');
      doctorAppointments.push({
        ...newAppointment,
        patientPhone: user.phone || '0555 123 45 67',
        patientAge: calculateAge(user.birthDate) || 25
      });
      localStorage.setItem(doctorAppointmentsKey, JSON.stringify(doctorAppointments));

      setAppointmentForm({ specialty: '', doctorId: '', date: '', time: '', notes: '' });
      alert('Randevunuz başarıyla oluşturuldu! Doktor onayı bekleniyor.');
    } catch (error) {
      alert('Randevu oluşturulurken hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  // Yaş hesaplama fonksiyonu
  const calculateAge = (birthDate) => {
    if (!birthDate) return null;
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    return age;
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
        {activeTab === 'appointments' && (
          <div className="appointments-list">
            <h3>Randevu Geçmişim</h3>
            {appointments.length === 0 ? (
              <div className="empty-state">
                <p>Henüz randevunuz bulunmuyor.</p>
                <p>Yeni randevu oluşturmak için "Yeni Randevu" sekmesini kullanın.</p>
              </div>
            ) : (
              <div className="appointments-grid">
                {appointments.map(appointment => (
                  <div key={appointment.id} className={`appointment-card medical-card ${getStatusClass(appointment.status)}`}>
                    <div className="appointment-header">
                      <h4>{appointment.doctorName}</h4>
                      <span className={`