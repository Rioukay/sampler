package rioko.graphabstraction.configurations;

import rioko.utilities.Log;

public class UnsignedIntConfiguration extends IntegerConfiguration {

	public UnsignedIntConfiguration() {
		super();
	}

	public UnsignedIntConfiguration(int toShow) throws BadConfigurationException {
		super(toShow);
	}
	
	@Override
	public void setValueConfiguration(Object value) throws BadConfigurationException {
		Integer num = Integer.class.cast(value);
		
		if(num < 0) {
			throw new BadConfigurationException("No negative value accepted");
		}
		
		super.setValueConfiguration(num);
	}
	
	@Override
	public UnsignedIntConfiguration copy() {
		try {
			return new UnsignedIntConfiguration(this.getConfiguration());
		} catch (BadConfigurationException e) {
			// Impossible Exception
			Log.exception(e);
			return null;
		}
	}

}
