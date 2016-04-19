function fixDates(model) {
	if (model) {
		if (model.date) {
			model.date = new Date(model.date);
		}
	}
}