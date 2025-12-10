import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const planCreateSchema = z.object({
  type: z.string({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }),
  name: z
    .string()
    .min(1, ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD)
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)),
  description: z.string().max(1000, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(1000)).optional().or(z.literal('')),
  monthlyPrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)),
  annualPrice: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)),
  maxListings: z.number().int().min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)).optional().nullable(),
  maxPhotosPerListing: z.number().int().min(1, ERROR_MESSAGES.VALIDATION.MIN_VALUE(1)),
  featuredListing: z.boolean().optional(),
  prioritySupport: z.boolean().optional(),
  promotedPlacement: z.boolean().optional(),
  commissionRate: z
    .number({ invalid_type_error: ERROR_MESSAGES.VALIDATION.INVALID_NUMBER })
    .min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0))
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_VALUE(100)),
});

export const planUpdateSchema = z.object({
  name: z.string().max(100, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(100)).optional(),
  description: z.string().max(1000, ERROR_MESSAGES.VALIDATION.MAX_LENGTH(1000)).optional(),
  monthlyPrice: z.number().min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)).optional(),
  annualPrice: z.number().min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)).optional(),
  maxListings: z.number().int().min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0)).optional().nullable(),
  maxPhotosPerListing: z.number().int().min(1, ERROR_MESSAGES.VALIDATION.MIN_VALUE(1)).optional(),
  featuredListing: z.boolean().optional(),
  prioritySupport: z.boolean().optional(),
  promotedPlacement: z.boolean().optional(),
  commissionRate: z
    .number()
    .min(0, ERROR_MESSAGES.VALIDATION.MIN_VALUE(0))
    .max(100, ERROR_MESSAGES.VALIDATION.MAX_VALUE(100))
    .optional(),
});

export default { planCreateSchema, planUpdateSchema };
