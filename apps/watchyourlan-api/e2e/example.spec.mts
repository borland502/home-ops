import {type APIResponse, expect, test} from "@playwright/test";
import Ajv from "ajv";
import * as fs from "node:fs";

// https://www.mikestreety.co.uk/blog/validate-a-json-api-with-playwright-and-json-schema/

test.describe("JSON Schema Validation", () => {
  test("is not undefined", async ({request}) => {
    const response: Response = await (await request.get('http://localhost:3001/api/hosts/all')).json();
    expect(response !== undefined)
  })

  const ajv = new Ajv();

  test("is valid schema", async ({request}) => {
    const response: APIResponse = (await request.get('http://localhost:3001/api/hosts/all'));
    const data = await response.json();

    let schema = JSON.parse(fs.readFileSync('apps/watchyourlan-api/src/schemas/watchyourlan.schema.json', 'utf-8'));

    const valid = ajv.validate(schema, data);

    if (!valid) {
      console.error(`Schema validation failed ${ajv.errorsText()}`);
    }

    expect(valid).toBe(true);
  });
})
