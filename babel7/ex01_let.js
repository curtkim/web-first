var links = [];
for(let i=0;i<5;i++) {
    links.push({onclick: function() { console.log('link: ', i); }});
}
 
links[0].onclick(); // link: 0
links[1].onclick(); // link: 1