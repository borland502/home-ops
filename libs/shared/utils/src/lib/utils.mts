export function utils(): string {
  return "utils";
}

export function isNil(value: unknown): boolean {
  return value == undefined || value == null;
}
