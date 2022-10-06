export interface ComplexPoint{
  readonly time: number;
  readonly x: number;
  readonly y: number;
  readonly line: string;
  readonly from: number;
  readonly to: number;
  readonly fraction: number;
  readonly nodes: Array<number>;
}