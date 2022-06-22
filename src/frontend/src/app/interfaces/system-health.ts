import { Component } from '@angular/core';
export interface SystemHealth {
    status: string;
    components: {
        db: {
            status: string,
            details: {
                database: string,
                validationQuery: string
            }
        },
        diskSpace: {
            status: string,
            details: {
                total: number,
                free: number | string,
                threshold: number,
                exits: boolean
            }
        }
    };
}