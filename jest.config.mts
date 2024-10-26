import { getJestProjectsAsync } from '@nx/jest';

export default async () => ({
  projects: [
    ...(await getJestProjectsAsync()),
    {
      preset: 'ts-jest',
      displayName: 'backend',
      testEnvironment: 'node',
      testMatch: ['<rootDir>/apps/backend/**/*.spec.ts'],
      moduleNameMapper: {
        '^@technohouser/(.*)$': '<rootDir>/libs/$1/src'
      },
      passWithNoTests: true
    }
  ],
});
