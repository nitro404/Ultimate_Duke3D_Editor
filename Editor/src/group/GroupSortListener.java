package group;

public interface GroupSortListener {
	
	public void handleGroupSortStateChanged(Group group);
	public void handleGroupSortStarted(Group group);
	public void handleGroupSortFinished(Group group);
	
}
