import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const addressCreateSchema = z.object({
  label: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50)),
  street: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)),
  complement: z
    .string()
    .max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200))
    .optional()
    .or(z.literal('')),
  city: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)),
  postalCode: z
    .string()
    .min(5, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(5))
    .max(10, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(10)),
  country: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50)),
  isDefault: z.boolean().optional(),
});

export const addressUpdateSchema = z.object({
  label: z.string().max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50)).optional(),
  street: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional(),
  complement: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional(),
  city: z.string().max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)).optional(),
  postalCode: z
    .string()
    .min(5, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(5))
    .max(10, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(10))
    .optional(),
  country: z.string().max(50, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(50)).optional(),
  isDefault: z.boolean().optional(),
});

export default { addressCreateSchema, addressUpdateSchema };
