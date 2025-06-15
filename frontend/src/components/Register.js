import React, { useState } from 'react';
import { userAPI } from '../services/api';
import './Register.css';

const Register = ({ onRegister, onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    email: '',
    password: '',
    confirmPassword: '',
    phoneNo: '',
    birthDate: '',
    role: 'hasta',
    specialty: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const specialties = [
    { value: 'Dermatology', label: 'Dermatoloji' },
    { value: 'Cardiology', label: 'Kardiyoloji' },
    { value: 'Eye', label: 'Göz Hastalıkları' },
    { value: 'General_Surgery', label: 'Genel Cerrahi' }
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Validasyonlar
      if (formData.password !== formData.confirmPassword) {
        throw new Error('Şifreler eşleşmiyor');
      }
      if (formData.password.length < 6) {
        throw new Error('Şifre en az 6 karakter olmalıdır');
      }
      if (formData.role === 'doktor' && !formData.specialty) {
        throw new Error('Doktor için uzmanlık alanı seçimi zorunludur');
      }
      if (!formData.birthDate) {
        throw new Error('Doğum tarihi zorunludur');
      }

      // Yaş kontrolü (18 yaş minimum)
      const birthDate = new Date(formData.birthDate);
      const today = new Date();
      const age = Math.floor((today - birthDate) / (365.25 * 24 * 60 * 60 * 1000));
      if (age < 18) {
        throw new Error('18 yaşından küçük kullanıcılar kayıt olamaz');
      }

      let registeredUser;

      if (formData.role === 'doktor') {
        // Doktor kayıt
        const doctorData = {
          email: formData.email,
          password: formData.password,
          name: formData.name,
          surname: formData.surname,
          birthDate: formData.birthDate,
          phoneNo: formData.phoneNo,
          specialty: formData.specialty
        };
        registeredUser = await userAPI.registerDoctor(doctorData);
      } else {
        // Hasta kayıt
        const patientData = {
          email: formData.email,
          password: formData.password,
          name: formData.name,
          surname: formData.surname,
          birthDate: formData.birthDate,
          phoneNo: formData.phoneNo
        };
        registeredUser = await userAPI.registerPatient(patientData);
      }
      
      // Frontend için user mapping
      const mappedUser = {
        ...registeredUser,
        id: registeredUser.userId,
        name: `${registeredUser.name} ${registeredUser.surname}`,
        role: formData.role,
        phone: registeredUser.phoneNo,
        birthDate: registeredUser.birthDate,
        specialty: registeredUser.specialty || null
      };

      onRegister(mappedUser);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
      // Role değiştiğinde specialty'yi sıfırla
      ...(name === 'role' && value === 'hasta' ? { specialty: '' } : {})
    });
  };

  return (
    <div className="register-container">
      <div className="register-card medical-card">
        <div className="register-header">
          <div className="medical-logo">⚕</div>
          <h2>Yeni Hesap</h2>
          <p>MediSys Hastane Yönetim Sistemi</p>
        </div>

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="name">Ad</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Adınızı girin"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="surname">Soyad</label>
              <input
                type="text"
                id="surname"
                name="surname"
                value={formData.surname}
                onChange={handleChange}
                placeholder="Soyadınızı girin"
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="email">E-posta Adresi</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="kullanici@hastane.com"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="phoneNo">Telefon</label>
              <input
                type="tel"
                id="phoneNo"
                name="phoneNo"
                value={formData.phoneNo}
                onChange={handleChange}
                placeholder="05XX XXX XX XX"
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="password">Şifre</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="En az 6 karakter"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="confirmPassword">Şifre Tekrar</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Şifrenizi tekrar girin"
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="birthDate">Doğum Tarihi</label>
            <input
              type="date"
              id="birthDate"
              name="birthDate"
              value={formData.birthDate}
              onChange={handleChange}
              max={new Date(new Date().setFullYear(new Date().getFullYear() - 18)).toISOString().split('T')[0]}
              required
            />
            <small className="form-note">18 yaşından büyük olmalısınız</small>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="role">Kullanıcı Tipi</label>
              <select
                id="role"
                name="role"
                value={formData.role}
                onChange={handleChange}
                className="role-select"
              >
                <option value="hasta">Hasta</option>
                <option value="doktor">Doktor</option>
              </select>
            </div>
            {formData.role === 'doktor' && (
              <div className="form-group">
                <label htmlFor="specialty">Uzmanlık Alanı</label>
                <select
                  id="specialty"
                  name="specialty"
                  value={formData.specialty}
                  onChange={handleChange}
                  className="specialty-select"
                  required
                >
                  <option value="">Uzmanlık alanınızı seçiniz...</option>
                  {specialties.map(specialty => (
                    <option key={specialty.value} value={specialty.value}>
                      {specialty.label}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          {error && <div className="error-message">{error}</div>}

          <button 
            type="submit" 
            className="register-btn btn btn-success"
            disabled={loading}
          >
            {loading ? 'Hesap oluşturuluyor...' : 'Hesap Oluştur'}
          </button>
        </form>

        <div className="register-footer">
          <p>Zaten hesabınız var mı?</p>
          <button 
            onClick={onSwitchToLogin}
            className="switch-btn"
          >
            Giriş Yap
          </button>
        </div>
      </div>
    </div>
  );
};

export default Register; 