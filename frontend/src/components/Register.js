import React, { useState } from 'react';
import './Register.css';

const Register = ({ onRegister, onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
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

      // E-posta kontrolü
      const existingUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]');
      if (existingUsers.find(user => user.email === formData.email)) {
        throw new Error('Bu e-posta adresi zaten kullanılıyor');
      }

      // API çağrısı simülasyonu
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Kayıt başarılı, kullanıcıyı oluştur
      const newUser = {
        id: Date.now(),
        name: formData.name,
        email: formData.email,
        role: formData.role,
        phone: formData.phone,
        birthDate: formData.birthDate,
        specialty: formData.role === 'doktor' ? formData.specialty : null
      };

      // Kullanıcıyı kayıt listesine ekle
      existingUsers.push(newUser);
      localStorage.setItem('registeredUsers', JSON.stringify(existingUsers));

      onRegister(newUser);
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

  const getSpecialtyLabel = (value) => {
    const specialty = specialties.find(s => s.value === value);
    return specialty ? specialty.label : value;
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
              <label htmlFor="name">Ad Soyad</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Adınızı ve soyadınızı girin"
                required
              />
            </div>
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

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="phone">Telefon Numarası</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="0555 123 45 67"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="birthDate">Doğum Tarihi</label>
              <input
                type="date"
                id="birthDate"
                name="birthDate"
                value={formData.birthDate}
                onChange={handleChange}
                required
              />
            </div>
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