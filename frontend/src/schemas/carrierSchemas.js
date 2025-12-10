import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

const codeRegex = /^[A-Z0-9_]+$/;

export const carrierCreateSchema = z.object({
  name: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)),
  code: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .regex(codeRegex, 'Code must contain only uppercase letters, numbers and underscores')
    .max(20, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(20)),
  logo: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional().or(z.literal('')),
  apiEndpoint: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional().or(z.literal('')),
  trackingUrlPattern: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional().or(z.literal('')),
  basePrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)),
  active: z.boolean().optional(),
  description: z.string().max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500)).optional(),
});

export const carrierUpdateSchema = z.object({
  name: z.string().max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)).optional(),
  code: z
    .string()
    .regex(codeRegex, 'Code must contain only uppercase letters, numbers and underscores')
    .max(20, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(20))
    .optional(),
  logo: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional(),
  apiEndpoint: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional(),
  trackingUrlPattern: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional(),
  basePrice: z.number().min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)).optional(),
  active: z.boolean().optional(),
  description: z.string().max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500)).optional(),
});

export default { carrierCreateSchema, carrierUpdateSchema };
