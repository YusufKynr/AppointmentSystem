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
      // App.js'deki handleLogin fonksiyonunu çağır (session API kullanacak)
      await onLogin(formData);
    } catch (err) {
      setError(err.message || 'Giriş başarısız. E-posta veya şifre hatalı.');
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
      <div className="login-card">
        <div className="login-left">
          <div className="login-header">
            <h2>Giriş Yap</h2>
            <p>Hesabınıza güvenli giriş yapın</p>
          </div>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="email">E-posta</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="E-posta adresinizi girin"
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

            <button 
              type="submit" 
              className="btn btn-primary login-btn"
              disabled={loading}
            >
              {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
            </button>
          </form>

          <div className="login-footer">
            <p>
              Hesabınız yok mu?{' '}
              <button 
                onClick={onSwitchToRegister}
                className="link-btn"
              >
                Kayıt Ol
              </button>
            </p>
          </div>
        </div>

        <div className="login-right">
          <div className="right-content">
            <div className="medical-logo">⚕</div>
            <h3>MediSys'e Hoş Geldiniz</h3>
            <p>Modern hastane yönetim sistemi ile sağlık hizmetlerinizi dijitalleştirin.</p>
            <ul className="features-list">
              <li>✅ Güvenli randevu sistemi</li>
              <li>✅ Hasta takip sistemi</li>
              <li>✅ Doktor yönetimi</li>
              <li>✅ 7/24 erişim</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login; 