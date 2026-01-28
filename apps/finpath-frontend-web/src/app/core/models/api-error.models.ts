export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: FieldError[];
}

export interface FieldError {
  field: string;
  message: string;
  rejectedValue: unknown;
}

export function isApiError(error: unknown): error is ApiError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    'error' in error &&
    'message' in error
  );
}

export function extractFieldErrors(error: ApiError): Map<string, string> {
  const errors = new Map<string, string>();
  if (error.fieldErrors) {
    for (const fieldError of error.fieldErrors) {
      errors.set(fieldError.field, fieldError.message);
    }
  }
  return errors;
}
