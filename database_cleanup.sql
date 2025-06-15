-- Database temizlik: null email'li kayıtları sil
USE medicaltracking;

-- Önce tüm appointment'ları sil (foreign key constraint için)
DELETE FROM appointment;

-- Null email'li kullanıcıları sil
DELETE FROM doctor WHERE user_id IN (SELECT user_id FROM user WHERE email IS NULL);
DELETE FROM patient WHERE user_id IN (SELECT user_id FROM user WHERE email IS NULL);
DELETE FROM user WHERE email IS NULL;

-- Auto increment'i sıfırla
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE doctor AUTO_INCREMENT = 1;
ALTER TABLE patient AUTO_INCREMENT = 1;
ALTER TABLE appointment AUTO_INCREMENT = 1; 