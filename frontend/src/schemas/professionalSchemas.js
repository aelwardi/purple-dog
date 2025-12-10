/**
 * Schémas de validation Zod pour l'inscription professionnelle
 */

import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

/**
 * Validation SIRET (14 chiffres)
 */
const siretRegex = /^\d{14}$/;

/**
 * Taille maximale de fichier (5MB)
 */
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

/**
 * Types de fichiers acceptés
 */
const ACCEPTED_FILE_TYPES = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];

/**
 * Validation de fichier
 */
const fileSchema = z
  .instanceof(File)
  .refine(
    (file) => file.size <= MAX_FILE_SIZE,
    ERROR_MESSAGES.VALIDATION.FILE_TOO_LARGE(5)
  )
  .refine(
    (file) => ACCEPTED_FILE_TYPES.includes(file.type),
    'Le fichier doit être au format PDF, JPG ou PNG'
  );

/**
 * Schéma de validation pour l'inscription professionnelle
 */
export const registerProfessionalSchema = z.object({
  // Informations personnelles
  firstName: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(2, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(2))
    .max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50)),
  lastName: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(2, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(2))
    .max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50)),
  email: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .email(ERROR_MESSAGES.VALIDATION.INVALID_EMAIL),
  
  // Informations entreprise
  companyName: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(2, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(2))
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)),
  siret: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .regex(siretRegex, ERROR_MESSAGES.VALIDATION.INVALID_SIRET)
    .transform((val) => val.replace(/\s/g, '')), // Enlever les espaces
  document: fileSchema.optional().or(z.null()),
  address: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10)),

  // Informations marketing
  website: z
    .string()
    .url(ERROR_MESSAGES.VALIDATION.INVALID_URL)
    .optional()
    .or(z.literal('')),
  specialties: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10))
    .max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500)),
  searchedObjects: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10))
    .max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500)),
  socialMedia: z
    .string()
    .max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200))
    .optional()
    .or(z.literal('')),

  // Sécurité
  password: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(8, ERROR_MESSAGES.AUTH.WEAK_PASSWORD),
  confirmPassword: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),

  // Confirmations
  cgvAccepted: z
    .boolean()
    .refine(val => val === true, 'Vous devez accepter les CGV'),
  mandateAccepted: z
    .boolean()
    .refine(val => val === true, 'Vous devez accepter le mandat d\'apport d\'affaire'),
  rgpdAccepted: z
    .boolean()
    .refine(val => val === true, 'Vous devez accepter les conditions RGPD'),
  newsletter: z.boolean().optional(),
}).refine(data => data.password === data.confirmPassword, {
  message: ERROR_MESSAGES.AUTH.PASSWORD_MISMATCH,
  path: ['confirmPassword'],
});

/**
 * Schéma pour la mise à jour du profil professionnel
 */
export const updateProfessionalSchema = z.object({
  firstName: z
    .string()
    .min(2, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(2))
    .max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50))
    .optional(),
  lastName: z
    .string()
    .min(2, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(2))
    .max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50))
    .optional(),
  companyName: z
    .string()
    .min(2, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(2))
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100))
    .optional(),
  address: z
    .string()
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10))
    .optional(),
  website: z
    .string()
    .url(ERROR_MESSAGES.VALIDATION.INVALID_URL)
    .optional()
    .or(z.literal('')),
  specialties: z
    .string()
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10))
    .max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500))
    .optional(),
  searchedObjects: z
    .string()
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10))
    .max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500))
    .optional(),
  socialMedia: z
    .string()
    .max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200))
    .optional(),
});

export default {
  registerProfessionalSchema,
  updateProfessionalSchema,
};
