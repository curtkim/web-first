export interface Letter {
  letter: String;
  yPos: number;
}
export interface Letters {
  ltrs: Letter[];
  intrvl: number;
}
export interface State {
  score: number;
  letters: Letter[];
  level: number;
}