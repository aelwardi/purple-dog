import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const supportTicketCreateSchema = z.object({
  subject: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(5, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(5))
    .max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)),
  description: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(10, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(10)),
});

export default { supportTicketCreateSchema };
