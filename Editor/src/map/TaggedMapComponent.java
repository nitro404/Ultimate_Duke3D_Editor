package map;

public abstract class TaggedMapComponent extends MapComponent implements TagInformationChangeListener {

	protected TagInformation m_tagInformation;

	public TaggedMapComponent(TagInformation tagInformation) {
		if(tagInformation == null) { throw new IllegalArgumentException("Tag information cannot be null."); }

		m_tagInformation = tagInformation;
	}

	public boolean hasLowTag() {
		return m_tagInformation.hasLowTag();
	}

	public int getLowTag() {
		return m_tagInformation.getLowTag();
	}

	public void setLowTag(int lowTag) throws IllegalArgumentException {
		m_tagInformation.setLowTag(lowTag);
	}

	public boolean hasHighTag() {
		return m_tagInformation.hasHighTag();
	}

	public int getHighTag() {
		return m_tagInformation.getHighTag();
	}

	public void setHighTag(int highTag) throws IllegalArgumentException {
		m_tagInformation.setHighTag(highTag);
	}

	public int getExtra() {
		return m_tagInformation.getExtra();
	}

	public void setExtra(int extra) {
		m_tagInformation.setExtra(extra);
	}

	public TagInformation getTagInformation() {
		return m_tagInformation;
	}
	
	public void setTagInformation(TagInformation tagInformation) throws IllegalArgumentException {
		if(tagInformation == null) {
			throw new IllegalArgumentException("Tag information cannot be null.");
		}

		if(m_tagInformation != null) {
			if(m_tagInformation.equals(tagInformation)) {
				return;
			}

			m_tagInformation.removeTagInformationChangeListener(this);
		}

		m_tagInformation = tagInformation;
		m_tagInformation.addTagInformationChangeListener(this);
	}

}
