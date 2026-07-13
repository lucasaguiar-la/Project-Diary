window.MOOD_COLORS = {
    feliz:    { base: '#F5B301', text: '#212529', label: 'Felicidade' },
    amor:     { base: '#D14E93', text: '#FFFFFF', label: 'Amor' },
    calmo:    { base: '#2E9E5B', text: '#FFFFFF', label: 'Calma' },
    surpresa: { base: '#E8710A', text: '#FFFFFF', label: 'Surpresa' },
    neutro:   { base: '#6C757D', text: '#FFFFFF', label: 'Neutralidade' },
    ansioso:  { base: '#343A40', text: '#FFFFFF', label: 'Ansiedade' },
    triste:   { base: '#4267B2', text: '#FFFFFF', label: 'Tristeza' },
    raiva:    { base: '#D64545', text: '#FFFFFF', label: 'Raiva' }
};

window.moodSoftBg = function (title, alpha) {
    const c = window.MOOD_COLORS[title];
    if (!c) return 'transparent';
    const h = c.base.replace('#', '');
    const r = parseInt(h.substring(0, 2), 16);
    const g = parseInt(h.substring(2, 4), 16);
    const b = parseInt(h.substring(4, 6), 16);
    return 'rgba(' + r + ',' + g + ',' + b + ',' + (alpha == null ? 0.14 : alpha) + ')';
};
