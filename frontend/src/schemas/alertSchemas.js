import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

const nonNegative = (min = 0) => ERROR_MESSAGES.VALIDATION.MIN_VALUE(min);

export const alertCreateSchema = z.object({
  userId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  categoryId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  keywords: z.string().optional().or(z.literal('')),
  minPrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0, nonNegative(0))
    .optional(),
  maxPrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0, nonNegative(0))
    .optional(),
  condition: z.string().optional(),
  emailNotification: z.boolean().optional(),
  inAppNotification: z.boolean().optional(),
});

export const alertUpdateSchema = z.object({
  keywords: z.string().optional(),
  minPrice: z.number().min(0, nonNegative(0)).optional(),
  maxPrice: z.number().min(0, nonNegative(0)).optional(),
  condition: z.string().optional(),
  active: z.boolean().optional(),
  emailNotification: z.boolean().optional(),
  inAppNotification: z.boolean().optional(),
});

export default { alertCreateSchema, alertUpdateSchema };
