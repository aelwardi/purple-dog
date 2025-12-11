import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const reviewCreateSchema = z.object({
  userId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  rating: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER }).min(1).max(5),
  title: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
  comment: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
});

export default { reviewCreateSchema };
