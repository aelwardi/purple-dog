import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const deliveryCreateSchema = z.object({
  orderId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  carrierId: z.number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }).int().positive(),
  trackingNumber: z.string().optional().or(z.literal('')),
  estimatedDeliveryDate: z.string().optional().or(z.literal('')),
  notes: z.string().optional().or(z.literal('')),
});

export const deliveryUpdateSchema = z.object({
  trackingNumber: z.string().optional(),
  status: z.string().optional(),
  labelUrl: z.string().max(200, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(200)).optional(),
  estimatedDeliveryDate: z.string().optional(),
  notes: z.string().max(1000, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(1000)).optional(),
});

export default { deliveryCreateSchema, deliveryUpdateSchema };
