import { z } from 'zod';
import { ERROR_MESSAGES } from '../utils/errorMessages';

export const supportTicketUpdateSchema = z.object({
  subject: z.string().optional(),
  description: z.string().optional(),
  status: z.string().optional(),
  priority: z.string().optional(),
  category: z.string().optional(),
  assignedAdminId: z.number().int().positive().optional(),
});

export default { supportTicketUpdateSchema };
