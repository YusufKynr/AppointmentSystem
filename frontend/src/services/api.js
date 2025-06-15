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

// Kullanıcı API'leri
export const userAPI = {
  // Kullanıcı giriş
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