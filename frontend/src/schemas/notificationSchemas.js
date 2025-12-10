import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const notificationCreateSchema = z.object({
  userId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  type: z.string({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }),
  title: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
  message: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
});

export default { notificationCreateSchema };
