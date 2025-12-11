import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const loginRequestSchema = z.object({
  email: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .email(ERROR_MESSAGES.VALIDATION.INVALID_EMAIL),
  password: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .min(6, ERROR_MESSAGES.VALIDATION.MIN_LENGTH(6)),
});

export default { loginRequestSchema };
