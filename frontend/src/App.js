import React, { useState } from 'react';
import './App.css';
import Login from './components/Login';
import PatientDashboard from './components/PatientDashboard';
import DoctorDashboard from './components/DoctorDashboard';
import Register from './components/Register';

function App() {
  const [user, setUser] = useState(null);
  const [currentView, setCurrentView] = useState('login');

  const handleLogin = (userData) => {
    setUser(userData);
    setCurrentView(userData.role === 'hasta' ? 'patient' : 'doctor');
  };

  const handleLogout = () => {
    setUser(null);
    setCurrentView('login');
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
