import React, { useState, useEffect } from 'react';
import './App.css';
import Login from './components/Login';
import PatientDashboard from './components/PatientDashboard';
import DoctorDashboard from './components/DoctorDashboard';
import Register from './components/Register';
import { sessionAPI } from './services/api';

function App() {
  const [user, setUser] = useState(null);
  const [currentView, setCurrentView] = useState('login');
  const [sessionToken, setSessionToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Sayfa yüklendiğinde session token'ı kontrol et
  useEffect(() => {
    const checkSession = async () => {
      try {
        // Session token'ı sessionStorage'dan al (sadece tarayıcı oturumu için)
        const savedToken = sessionStorage.getItem('sessionToken');
        
        if (savedToken) {
          // Session'ı database'den doğrula
          const sessionData = await sessionAPI.validateSession(savedToken);
          
          if (sessionData.valid) {
            // Session geçerli, kullanıcı bilgilerini ayarla
            const userData = {
              ...sessionData.user,
              id: sessionData.user.userId,
              role: sessionData.user.role === 'PATIENT' ? 'hasta' : 'doktor'
            };
            
            setUser(userData);
            setSessionToken(savedToken);
            setCurrentView(userData.role === 'hasta' ? 'patient' : 'doctor');
          } else {
            // Session geçersiz, temizle
            sessionStorage.removeItem('sessionToken');
            setSessionToken(null);
            setUser(null);
            setCurrentView('login');
          }
        }
      } catch (error) {
        console.error('Session kontrol hatası:', error);
        // Hata durumunda session'ı temizle
        sessionStorage.removeItem('sessionToken');
        setSessionToken(null);
        setUser(null);
        setCurrentView('login');
      } finally {
        setLoading(false);
      }
    };

    checkSession();
  }, []);

  // Session yenileme (her 30 dakikada bir)
  useEffect(() => {
    if (sessionToken) {
      const refreshInterval = setInterval(async () => {
        try {
          const refreshedSession = await sessionAPI.refreshSession(sessionToken);
          setSessionToken(refreshedSession.sessionToken);
          sessionStorage.setItem('sessionToken', refreshedSession.sessionToken);
        } catch (error) {
          console.error('Session yenileme hatası:', error);
          handleLogout();
        }
      }, 30 * 60 * 1000); // 30 dakika

      return () => clearInterval(refreshInterval);
    }
  }, [sessionToken]);

  const handleLogin = async (loginData) => {
    try {
      setLoading(true);
      
      // Database'den session oluştur
      const sessionData = await sessionAPI.login(loginData.email, loginData.password);
      
      // Kullanıcı bilgilerini düzenle
      const userData = {
        ...sessionData.user,
        id: sessionData.user.userId,
        role: sessionData.user.role === 'PATIENT' ? 'hasta' : 'doktor'
      };
      
      // State'leri güncelle
      setUser(userData);
      setSessionToken(sessionData.sessionToken);
      
      // Session token'ı sessionStorage'a kaydet
      sessionStorage.setItem('sessionToken', sessionData.sessionToken);
      
      // View'ı ayarla
      const viewType = userData.role === 'hasta' ? 'patient' : 'doctor';
      setCurrentView(viewType);
      
    } catch (error) {
      console.error('Giriş hatası:', error);
      throw error; // Login component'ine hata gönder
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      if (sessionToken) {
        // Database'den session'ı geçersiz kıl
        await sessionAPI.logout(sessionToken);
      }
    } catch (error) {
      console.error('Çıkış hatası:', error);
    } finally {
      // Local state'i temizle
      setUser(null);
      setCurrentView('login');
      setSessionToken(null);
      
      // SessionStorage'ı temizle
      sessionStorage.removeItem('sessionToken');
    }
  };

  const handleRegister = (userData) => {
    // Kayıt sonrası otomatik giriş yapmak için login fonksiyonunu çağır
    handleLogin({ email: userData.email, password: userData.password });
  };

  const renderCurrentView = () => {
    if (loading) {
      return (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Yükleniyor...</p>
        </div>
      );
    }

    switch (currentView) {
      case 'login':
        return (
          <Login 
            onLogin={handleLogin} 
            onSwitchToRegister={() => setCurrentView('register')}
          />
        );
      case 'register':
        return (
          <Register 
            onRegister={handleRegister}
            onSwitchToLogin={() => setCurrentView('login')}
          />
        );
      case 'patient':
        return <PatientDashboard user={user} onLogout={handleLogout} />;
      case 'doctor':
        return <DoctorDashboard user={user} onLogout={handleLogout} />;
      default:
        return (
          <Login 
            onLogin={handleLogin} 
            onSwitchToRegister={() => setCurrentView('register')}
          />
        );
    }
  };

  return (
    <div className="App">
      <header className="app-header medical-header">
        <h1>MediSys - Hastane Yönetim Sistemi</h1>
        {user && (
          <div className="user-info">
            <span>Hoş geldiniz, {user.name}</span>
            <button onClick={handleLogout} className="logout-btn">
              Güvenli Çıkış
            </button>
          </div>
        )}
      </header>
      <main className="app-main">
        {renderCurrentView()}
      </main>
    </div>
  );
}

export default App;
