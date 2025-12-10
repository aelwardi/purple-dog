import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const documentUploadSchema = z.object({
  fileName: z.string().optional(),
  fileUrl: z.string().url(ERROR_MESSAGES.VALIDATION.INVALID_URL).optional(),
  documentType: z.string({ invalid_type_error: ERROR_MESSAGES.VALIDATION.REQUIRED_FIELD }),
  description: z.string().optional(),
});

export default { documentUploadSchema };
