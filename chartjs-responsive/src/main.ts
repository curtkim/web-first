import './style.css'
import { Chart, registerables} from 'chart.js';
Chart.register(...registerables);

const ctx: HTMLCanvasElement = document.getElementById('chart');

const chart = new Chart(ctx, {
		type: 'bar',
		data: {
				labels: ['Nerds', 'Runts', 'Jolly Ranchers', 'Sweet Tarts', 'Lemonheads', 'Necco Wafers', 'Gobstoppers'],
				datasets: [{
					label: '# of Votes',
					data: [12, 19, 22, 5, 2, 1, 10],
					backgroundColor: '#49708A'
				}]
		},
		options: {
      responsive: true,
      maintainAspectRatio: false, // default is `true`, default `aspectRatio` is 2
			scales: {
				y: {
					beginAtZero: true,
					grid: {
						color: '#000',
						drawBorder: false
					}
				},
				x: {
					grid: {
						display: false,
					}
				}
			},
		}
});
