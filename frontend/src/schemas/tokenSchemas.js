import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const refreshTokenSchema = z.object({
  refreshToken: z.string().min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD),
});

export default { refreshTokenSchema };
