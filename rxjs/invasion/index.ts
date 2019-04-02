console.clear();
import { interval, fromEvent, combineLatest, BehaviorSubject } from 'rxjs';
import { scan, startWith, map, takeWhile, switchMap } from 'rxjs/operators';
import { State, Letter, Letters } from './interfaces';

const randomLetter = () => String.fromCharCode(
  Math.random() * ('z'.charCodeAt(0) - 'a'.charCodeAt(0)) + 'a'.charCodeAt(0));
const levelChangeThreshold = 20;
const speedAdjust = 50;
const endThreshold = 15;
const gameWidth = 30;

const intervalSubject = new BehaviorSubject(600);

const letters$ = intervalSubject.pipe(
  switchMap(i => interval(i)
    .pipe(
      scan<number, Letters>((letters) => ({
        intrvl: i,
        ltrs: [({
          letter: randomLetter(),
          yPos: Math.floor(Math.random() * gameWidth)
        }), ...letters.ltrs]
      }), { ltrs: [], intrvl: 0 })
    )));

const keys$ = fromEvent(document, 'keydown')
  .pipe(
    startWith({ key: '' }),
    map((e: KeyboardEvent) => e.key)
  );

const renderGame = (state: State) => (
  document.body.innerHTML = `Score: ${state.score}, Level: ${state.level} <br/>`,
  state.letters.forEach(l => document.body.innerHTML +=
    '&nbsp'.repeat(l.yPos) + l.letter + '<br/>'),
  document.body.innerHTML +=
  '<br/>'.repeat(endThreshold - state.letters.length - 1) + '-'.repeat(gameWidth)
);
const renderGameOver = () => document.body.innerHTML += '<br/>GAME OVER!';
const noop = () => { };

const game$ = combineLatest(keys$, letters$).pipe(
  scan<[string, Letters], State>((state, [key, letters]) => (
    letters.ltrs[letters.ltrs.length - 1]
      && letters.ltrs[letters.ltrs.length - 1].letter === key
      ? (state.score = state.score + 1, letters.ltrs.pop())
      : noop,
    state.score > 0 && state.score % levelChangeThreshold === 0
      ? (
        letters.ltrs = [],
        state.level = state.level + 1,
        state.score = state.score + 1,
        intervalSubject.next(letters.intrvl - speedAdjust))
      : noop,
    ({ score: state.score, letters: letters.ltrs, level: state.level })),
    { score: 0, letters: [], level: 1 }),
  takeWhile(state => state.letters.length < endThreshold),
)

game$.subscribe(
  renderGame,
  noop,
  renderGameOver
);