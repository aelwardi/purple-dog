import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const quickSaleCreateSchema = z.object({
  productId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  fixedPrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0.01, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0.01)),
  minimumOfferPrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0.01, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0.01))
    .optional(),
});

export const quickSaleUpdateSchema = z.object({
  price: z.number().min(0.01, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0.01)).optional(),
  minimumOfferPrice: z.number().min(0.01, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0.01)).optional(),
});

export default { quickSaleCreateSchema, quickSaleUpdateSchema };
