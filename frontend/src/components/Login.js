import React, { useState } from 'react';
import './Login.css';

const Login = ({ onLogin, onSwitchToRegister }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // API çağrısı simülasyonu
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Sadece kayıt olan kullanıcıları kontrol et
      const registeredUsers = JSON.parse(localStorage.getItem('registeredUsers') || '[]');
      const foundUser = registeredUsers.find(u => u.email === formData.email);

      if (foundUser && formData.password === '123456') { // Basit şifre kontrolü (gerçek uygulamada hash kullanılır)
        onLogin(foundUser);
      } else {
        throw new Error('Geçersiz kullanıcı adı veya şifre');
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="login-container">
      <div className="login-card medical-card">
        {/* Sol Kolon - Form */}
        <div className="login-left">
          <div className="login-header">
            <h2>Sistem Girişi</h2>
            <p>MediSys Hastane Yönetim Sistemi</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
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
              <label htmlFor="password">Şifre</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Şifrenizi girin"
                required
              />
            </div>

            {error && <div className="error-message">{error}</div>}

            <button 
              type="submit" 
              className="login-btn btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Giriş yapılıyor...' : 'Güvenli Giriş'}
            </button>
          </form>

          <div className="login-footer">
            <p>Hesabınız yok mu?</p>
            <button 
              onClick={onSwitchToRegister}
              className="switch-btn"
            >
              Yeni Hesap Oluştur
            </button>
          </div>
        </div>

        {/* Sağ Kolon - Logo ve Bilgiler */}
        <div className="login-right">
          <div className="right-content">
            <div className="medical-logo">⚕</div>
            <h3>MediSys</h3>
            <p>Modern hastane yönetim sistemi ile sağlık hizmetlerinizi dijitalleştirin</p>
            
            <ul className="features-list">
              <li>Randevu yönetimi</li>
              <li>Hasta takip sistemi</li>
              <li>Doktor paneli</li>
              <li>Güvenli veri saklama</li>
              <li>Mobil uyumlu tasarım</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login; 