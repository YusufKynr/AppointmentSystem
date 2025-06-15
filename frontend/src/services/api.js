import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081';

// Axios instance oluştur
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 saniye timeout
});

// Response interceptor ile hata yönetimi
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Hatası:', error);
    return Promise.reject(error);
  }
);

// Session Management API'leri
export const sessionAPI = {
  // Database tabanlı giriş
  login: async (email, password) => {
    try {
      const response = await apiClient.post('/session/login', {
        email,
        password
      });
      return response.data;
    } catch (error) {
      throw new Error('Giriş başarısız. E-posta veya şifre hatalı.');
    }
  },

  // Session doğrulama
  validateSession: async (sessionToken) => {
    try {
      const response = await apiClient.post('/session/validate', {
        sessionToken
      });
      return response.data;
    } catch (error) {
      throw new Error('Session doğrulanamadı.');
    }
  },

  // Çıkış yapma
  logout: async (sessionToken) => {
    try {
      const response = await apiClient.post('/session/logout', {
        sessionToken
      });
      return response.data;
    } catch (error) {
      throw new Error('Çıkış işlemi başarısız.');
    }
  },

  // Session yenileme
  refreshSession: async (sessionToken) => {
    try {
      const response = await apiClient.post('/session/refresh', {
        sessionToken
      });
      return response.data;
    } catch (error) {
      throw new Error('Session yenilenemedi.');
    }
  }
};

// Kullanıcı API'leri
export const userAPI = {
  // Kullanıcı giriş (eski versiyon - artık sessionAPI.login kullanılacak)
  login: async (email, password) => {
    try {
      const response = await apiClient.post('/user/login', {
        email,
        password
      });
      return response.data;
    } catch (error) {
      throw new Error('Giriş başarısız. E-posta veya şifre hatalı.');
    }
  },

  // Basit kullanıcı kayıt (eski versiyon)
  register: async (userData) => {
    try {
      const response = await apiClient.post('/user/register', userData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error('Bu e-posta adresi zaten kullanılıyor.');
      }
      throw new Error('Kayıt işlemi başarısız.');
    }
  },

  // Doktor kayıt
  registerDoctor: async (doctorData) => {
    try {
      const response = await apiClient.post('/user/registerDoctor', doctorData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error('Bu e-posta adresi zaten kullanılıyor veya bilgiler hatalı.');
      }
      throw new Error('Doktor kayıt işlemi başarısız.');
    }
  },

  // Hasta kayıt
  registerPatient: async (patientData) => {
    try {
      const response = await apiClient.post('/user/registerPatient', patientData);
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error('Bu e-posta adresi zaten kullanılıyor veya bilgiler hatalı.');
      }
      throw new Error('Hasta kayıt işlemi başarısız.');
    }
  },

  // Kullanıcı bilgilerini getir
  getUser: async (userId) => {
    try {
      const response = await apiClient.get(`/user/getUser/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error('Kullanıcı bilgileri alınamadı.');
    }
  },

  // Tüm kullanıcıları getir
  getAllUsers: async () => {
    try {
      const response = await apiClient.get('/user/getAllUser');
      return response.data;
    } catch (error) {
      throw new Error('Kullanıcı listesi alınamadı.');
    }
  },

  // Tüm doktorları getir
  getAllDoctors: async () => {
    try {
      const response = await apiClient.get('/user/getAllDoctors');
      return response.data;
    } catch (error) {
      throw new Error('Doktor listesi alınamadı.');
    }
  },

  // Uzmanlık alanına göre doktor getir
  getDoctorsBySpecialty: async (specialty) => {
    try {
      const response = await apiClient.get(`/user/getDoctorsBySpecialty/${specialty}`);
      return response.data;
    } catch (error) {
      throw new Error('Uzmanlık alanına göre doktor listesi alınamadı.');
    }
  },

  // Tüm hastaları getir
  getAllPatients: async () => {
    try {
      const response = await apiClient.get('/user/getAllPatients');
      return response.data;
    } catch (error) {
      throw new Error('Hasta listesi alınamadı.');
    }
  },

  // Kullanıcı güncelle
  updateUser: async (userId, userData) => {
    try {
      const response = await apiClient.put(`/user/update/${userId}`, userData);
      return response.data;
    } catch (error) {
      throw new Error('Kullanıcı güncellenemedi.');
    }
  }
};

// Randevu API'leri
export const appointmentAPI = {
  // Randevu oluştur
  createAppointment: async (doctorId, patientId, appointmentTime, doctorNote = '') => {
    try {
      const response = await apiClient.post('/appointment/create', {
        doctorId: parseInt(doctorId),
        patientId: parseInt(patientId),
        appointmentTime: appointmentTime,
        doctorNote: doctorNote
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 400) {
        throw new Error('Randevu oluşturulamadı. Doktor bu saatte başka bir randevuya sahip olabilir.');
      }
      throw new Error('Randevu oluşturma işlemi başarısız.');
    }
  },

  // Hasta randevularını getir
  getPatientAppointments: async (patientId) => {
    try {
      const response = await apiClient.get(`/appointment/patient/${patientId}`);
      return response.data;
    } catch (error) {
      throw new Error('Hasta randevuları alınamadı.');
    }
  },

  // Doktor randevularını getir
  getDoctorAppointments: async (doctorId) => {
    try {
      const response = await apiClient.get(`/appointment/doctor/${doctorId}`);
      return response.data;
    } catch (error) {
      throw new Error('Doktor randevuları alınamadı.');
    }
  },

  // Randevu onayala
  approveAppointment: async (appointmentId) => {
    try {
      const response = await apiClient.post(`/appointment/approve/${appointmentId}`);
      return response.data;
    } catch (error) {
      throw new Error('Randevu onaylanamadı.');
    }
  },

  // Randevu reddet
  rejectAppointment: async (appointmentId) => {
    try {
      const response = await apiClient.post(`/appointment/reject/${appointmentId}`);
      return response.data;
    } catch (error) {
      throw new Error('Randevu reddedilemedi.');
    }
  },

  // Randevu iptal et
  cancelAppointment: async (appointmentId) => {
    try {
      const response = await apiClient.delete(`/appointment/cancel/${appointmentId}`);
      return response.data;
    } catch (error) {
      throw new Error('Randevu iptal edilemedi.');
    }
  },

  // Randevu notu ekle
  setAppointmentNote: async (appointmentId, doctorNote) => {
    try {
      const response = await apiClient.post(`/appointment/setNote/${appointmentId}`, {
        doctorNote: doctorNote
      });
      return response.data;
    } catch (error) {
      throw new Error('Randevu notu eklenemedi.');
    }
  }
};

// Backend'in çalışıp çalışmadığını kontrol et
export const checkBackendHealth = async () => {
  try {
    await apiClient.get('/user/getAllUser');
    return true;
  } catch (error) {
    console.error('Backend bağlantı hatası:', error);
    return false;
  }
};

export default apiClient; 