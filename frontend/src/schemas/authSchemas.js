/**
 * Schémas de validation Zod pour l'authentification
 */

import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

/**
 * Schéma de validation pour la connexion
 */
export const loginSchema = z.object({
  email: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .email(ERROR_MESSAGES.VALIDATION.INVALID_EMAIL),
  password: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(8, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(8)),
});

/**
 * Schéma de validation pour l'inscription individuel
 */
export const registerIndividualSchema = z.object({
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
  phone: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .regex(/^(\+33|0)[1-9](\d{2}){4}$/, ERROR_MESSAGES.VALIDATION.INVALID_PHONE)
    .optional()
    .or(z.literal('')),
  address: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10)),
  password: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(8, ERROR_MESSAGES.AUTH.WEAK_PASSWORD),
  confirmPassword: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
  cgvAccepted: z
    .boolean()
    .refine(val => val === true, 'Vous devez accepter les CGV'),
  newsletter: z.boolean().optional(),
}).refine(data => data.password === data.confirmPassword, {
  message: ERROR_MESSAGES.AUTH.PASSWORD_MISMATCH,
  path: ['confirmPassword'],
});

/**
 * Schéma pour mot de passe oublié
 */
export const forgotPasswordSchema = z.object({
  email: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .email(ERROR_MESSAGES.VALIDATION.INVALID_EMAIL),
});

/**
 * Schéma pour réinitialisation de mot de passe
 */
export const resetPasswordSchema = z.object({
  password: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(8, ERROR_MESSAGES.AUTH.WEAK_PASSWORD),
  confirmPassword: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
}).refine(data => data.password === data.confirmPassword, {
  message: ERROR_MESSAGES.AUTH.PASSWORD_MISMATCH,
  path: ['confirmPassword'],
});

/**
 * Schéma pour changement de mot de passe
 */
export const changePasswordSchema = z.object({
  currentPassword: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
  newPassword: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(8, ERROR_MESSAGES.AUTH.WEAK_PASSWORD),
  confirmNewPassword: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
}).refine(data => data.newPassword === data.confirmNewPassword, {
  message: ERROR_MESSAGES.AUTH.PASSWORD_MISMATCH,
  path: ['confirmNewPassword'],
}).refine(data => data.currentPassword !== data.newPassword, {
  message: 'Le nouveau mot de passe doit être différent de l\'ancien',
  path: ['newPassword'],
});

export default {
  loginSchema,
  registerIndividualSchema,
  forgotPasswordSchema,
  resetPasswordSchema,
  changePasswordSchema,
};
