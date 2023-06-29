export interface StepDataType {
  interfaceName: string;
  payAccount: string;
  receiverName: string;
  num: string;
  receiverMode: string;
}

export type CurrentTypes = 'base' | 'confirm' | 'result';
