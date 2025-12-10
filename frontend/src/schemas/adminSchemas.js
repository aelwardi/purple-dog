import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const adminCreateSchema = z.object({
  email: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .email(ERROR_MESSAGES.VALIDATION.INVALID_EMAIL),
  password: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(8, ERROR_MESSAGES.AUTH.WEAK_PASSWORD),
  firstName: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
  lastName: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
  phone: z.string().optional().or(z.literal('')),
  profilePicture: z.string().url(ERROR_MESSAGES.VALIDATION.INVALID_URL).optional().or(z.literal('')),
  bio: z.string().optional(),
  superAdmin: z.boolean().optional(),
  permissions: z.string().optional(),
});

export default { adminCreateSchema };
