import {expect, test} from "@playwright/test";

// https://www.mikestreety.co.uk/blog/validate-a-json-api-with-playwright-and-json-schema/

test.describe("Hello Express Validation", () => {
  test("is not undefined", async ({request}) => {
    const response: Response = await (await request.get('http://localhost:3333/')).json();
    expect(response !== undefined)
  })
})
