package com.readify.teamCity;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.WebLinks;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRoot;

import com.intellij.openapi.diagnostic.Logger;

public class TfsNotifier implements Notificator {

	private static final String tfsProjectName = "Voyager";
	private static final String tfsPassword = "Password13579!";
	private static final String tfsUsername = "ducas.francis";
	private static final String tfsUrl = "https://tfs.f-dev.net/tfs/Fiserv";

	private static final Logger LOG = Logger.getInstance(TfsNotifier.class.getName());
    
    /** Holds the user configuration properties */
//    private ArrayList<UserPropertyInfo> userProps;
    
    private static final String TYPE = "tcTfsNotifier";
//    private static final String TFS_SERVER_KEY = "tfsnotifier.tfsServer";
//    private static final String TFS_PASSWORD_KEY = "tfsnotifier.tfsPassword";
    public static final String APP_NAME = "Team City";
        
//    private static final PropertyKey SERVER_KEY = new NotificatorPropertyKey(TYPE, TFS_SERVER_KEY);
//    private static final PropertyKey PASSWORD_KEY = new NotificatorPropertyKey(TYPE, TFS_PASSWORD_KEY);
    
    private static final Pattern pattern = Pattern.compile("TFS(?:\\s?)(\\d+)");

	private WebLinks webLinks;
    
    public TfsNotifier (NotificatorRegistry notificatorRegistry, WebLinks webLinks){
		this.webLinks = webLinks;
    }
	
    @Override
	public String getDisplayName() {
		return "TFS Notifier";
	}

	@Override
	public String getNotificatorType() {
		return TYPE;
	}

	@Override
	public void notifyBuildFailed(SRunningBuild build, Set<SUser> users) {
		// TODO Auto-generated method stub
		LOG.debug("Build Failed #" + build.getBuildNumber());
		performNotification(build);
	}

	@Override
	public void notifyBuildFailedToStart(SRunningBuild build, Set<SUser> users) {
	}

	@Override
	public void notifyBuildFailing(SRunningBuild build, Set<SUser> users) {
	}

	@Override
	public void notifyBuildProbablyHanging(SRunningBuild build, Set<SUser> users) {
	}

	@Override
	public void notifyBuildStarted(SRunningBuild build, Set<SUser> users) {
		// TODO Auto-generated method stub
		LOG.debug("Build Started #" + build.getBuildNumber());
		performNotification(build);
	}

	@Override
	public void notifyBuildSuccessful(SRunningBuild build, Set<SUser> users) {
		// TODO Auto-generated method stub
		LOG.debug("Build Successful #" + build.getBuildNumber());
		performNotification(build);
	}

	private void performNotification(SRunningBuild build) {
		
		String buildNumber = build.getBuildNumber();
		String status = build.getStatusDescriptor().getStatus().getText();

		String buildUrl = webLinks.getViewLogUrl(build);
		String buildComment = status + " Build #" + buildNumber;
		
		HashMap<Integer, List<Modification>> modifications = buildModificationMap(build, buildUrl, buildComment);
		
		updateTfsWorkItems(modifications);
	}

	private HashMap<Integer, List<Modification>> buildModificationMap(SRunningBuild build, String hyperlinkUrl, String hyperlinkComment) {
		List<SVcsModification> changes = build.getChanges(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD, true);
		
		HashMap<Integer, List<Modification>> modifications = new HashMap<Integer, List<Modification>>();
		
		for (SVcsModification change : changes) {
			List<Integer> tfsIds = findTfsIds(change.getDescription());
			for (Integer id : tfsIds) {
				if (!modifications.containsKey(id)){
					List<Modification> itemModifications = new ArrayList<Modification>();
					itemModifications.add(new AddHyperlinkModification(id, hyperlinkUrl, hyperlinkComment));
					modifications.put(id, itemModifications);
				}
			}
		}
		return modifications;
	}

	private void updateTfsWorkItems(Map<Integer, List<Modification>> modifications) {
		try {
			TfsWorkItemManipulator tfs = new TfsWorkItemManipulator();
			tfs.open(tfsUrl, tfsUsername, tfsPassword, tfsProjectName);
			tfs.updateWorkItems(modifications);
			tfs.close();
		} catch (URISyntaxException e) {
			LOG.error("Failed to connect to TFS.", e);
		}
	}

	private List<Integer> findTfsIds(String description) {
		Matcher matcher = pattern.matcher(description);
		List<Integer> tfsIds = new ArrayList<Integer>();
		while (matcher.find()) {
			tfsIds.add(Integer.parseInt(matcher.group(1)));
		}
		return tfsIds;
	}

	@Override
	public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable,
			Set<SUser> users) {
	}

	@Override
	public void notifyResponsibleAssigned(SBuildType buildType, Set<SUser> users) {
	}

	@Override
	public void notifyResponsibleAssigned(TestNameResponsibilityEntry entry1,
			TestNameResponsibilityEntry entry2, SProject project, Set<SUser> users) {
	}

	@Override
	public void notifyResponsibleAssigned(Collection<TestName> tests,
			ResponsibilityEntry entry, SProject project, Set<SUser> users) {
	}

	@Override
	public void notifyResponsibleChanged(SBuildType buildType, Set<SUser> users) {
	}

	@Override
	public void notifyResponsibleChanged(TestNameResponsibilityEntry entry1,
			TestNameResponsibilityEntry entry2, SProject project, Set<SUser> users) {
	}

	@Override
	public void notifyResponsibleChanged(Collection<TestName> tests,
			ResponsibilityEntry entry, SProject project, Set<SUser> users) {
	}

	@Override
	public void notifyTestsMuted(Collection<STest> tests, MuteInfo muteInfo,
			Set<SUser> arg2) {
	}

	@Override
	public void notifyTestsUnmuted(Collection<STest> tests, MuteInfo muteInfo,
			SUser user, Set<SUser> users) {
	}

}
