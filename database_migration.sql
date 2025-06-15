-- Database migration: address sütunlarını birth_date olarak değiştir
-- Bu SQL'i MySQL'de çalıştırın

USE medicaltracking;

-- Doctor tablosunda address sütununu birth_date olarak değiştir
ALTER TABLE doctor 
CHANGE COLUMN address birth_date DATE;

-- Patient tablosunda address sütununu birth_date olarak değiştir  
ALTER TABLE patient
CHANGE COLUMN address birth_date DATE;

-- Mevcut verileri temizle (çünkü address string'den date'e çevirmek mümkün değil)
UPDATE doctor SET birth_date = '1990-01-01' WHERE birth_date IS NULL;
UPDATE patient SET birth_date = '1990-01-01' WHERE birth_date IS NULL; 