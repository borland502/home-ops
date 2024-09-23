/**
 * Shared types for routes.
 */

import { HttpStatusCodes } from "@technohouser/utils";
import { Response, Request } from "express";

// **** Express **** //

type TObj = Record<string, unknown>;


export interface Dto {
	type: string;
	id: number;
	attributes: {
		date: string | undefined;
		known: number | undefined;
		ip: string | undefined;
		now: number | undefined;
		name: string;
		mac: string | undefined;
		hw: string | undefined
	};
}
