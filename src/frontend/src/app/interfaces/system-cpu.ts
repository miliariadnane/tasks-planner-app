export interface SystemCpu {
    name: string;
    description: string;
    baseUnit: any;
    measurements: [
      {
        statistic: string,
        value: number
      }
    ];
    availableTags: any[];
  }