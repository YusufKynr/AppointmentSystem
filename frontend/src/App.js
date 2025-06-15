import React, { useState, useEffect } from 'react';
import './App.css';
import Login from './components/Login';
import PatientDashboard from './components/PatientDashboard';
import DoctorDashboard from './components/DoctorDashboard';
import Register from './components/Register';

function App() {
  const [user, setUser] = useState(null);
  const [currentView, setCurrentView] = useState('login');

  // Sayfa yüklendiğinde localStorage'dan kullanıcı bilgilerini kontrol et
  useEffect(() => {
    const savedUser = localStorage.getItem('currentUser');
    const savedView = localStorage.getItem('currentView');
    
    if (savedUser && savedView) {
      try {
        const parsedUser = JSON.parse(savedUser);
        setUser(parsedUser);
        setCurrentView(savedView);
      } catch (error) {
        console.error('Kullanıcı bilgileri okunurken hata:', error);
        // Hatalı veri varsa temizle
        localStorage.removeItem('currentUser');
        localStorage.removeItem('currentView');
      }
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    const viewType = userData.role === 'hasta' ? 'patient' : 'doctor';
    setCurrentView(viewType);
    
    // Kullanıcı bilgilerini localStorage'a kaydet
    localStorage.setItem('currentUser', JSON.stringify(userData));
    localStorage.setItem('currentView', viewType);
  };

  const handleLogout = () => {
    setUser(null);
    setCurrentView('login');
    
    // localStorage'dan kullanıcı bilgilerini temizle
    localStorage.removeItem('currentUser');
    localStorage.removeItem('currentView');
  };

  const renderCurrentView = () => {
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
            onRegister={handleLogin}
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
