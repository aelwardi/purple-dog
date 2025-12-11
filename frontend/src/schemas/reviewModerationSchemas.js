import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const reviewModerationSchema = z.object({
  adminId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  status: z.string({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }),
  adminResponse: z.string().optional(),
});

export default { reviewModerationSchema };
