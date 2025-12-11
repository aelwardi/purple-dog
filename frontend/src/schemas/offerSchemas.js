import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const offerCreateSchema = z.object({
  quickSaleId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  buyerId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  amount: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0.01, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0.01)),
  message: z.string().max(500, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(500)).optional().or(z.literal('')),
});

export default { offerCreateSchema };
