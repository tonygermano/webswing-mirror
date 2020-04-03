package org.webswing.server.services.rest.resources.model;

import java.util.ArrayList;
import java.util.List;
import org.webswing.server.services.rest.resources.model.SwingSession;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class Sessions   {
  
  private  List<SwingSession> sessions = new ArrayList<SwingSession>();
  private  List<SwingSession> closedSessions = new ArrayList<SwingSession>();
  private  List<String> recordings = new ArrayList<String>();

  /**
   **/
  public Sessions sessions(List<SwingSession> sessions) {
    this.sessions = sessions;
    return this;
  }

  
  @JsonProperty("sessions")
  public List<SwingSession> getSessions() {
    return sessions;
  }
  public void setSessions(List<SwingSession> sessions) {
    this.sessions = sessions;
  }

  /**
   **/
  public Sessions closedSessions(List<SwingSession> closedSessions) {
    this.closedSessions = closedSessions;
    return this;
  }

  
  @JsonProperty("closedSessions")
  public List<SwingSession> getClosedSessions() {
    return closedSessions;
  }
  public void setClosedSessions(List<SwingSession> closedSessions) {
    this.closedSessions = closedSessions;
  }

  /**
   **/
  public Sessions recordings(List<String> recordings) {
    this.recordings = recordings;
    return this;
  }

  
  @JsonProperty("recordings")
  public List<String> getRecordings() {
    return recordings;
  }
  public void setRecordings(List<String> recordings) {
    this.recordings = recordings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Sessions sessions = (Sessions) o;
    return Objects.equals(this.sessions, sessions.sessions) &&
        Objects.equals(this.closedSessions, sessions.closedSessions) &&
        Objects.equals(this.recordings, sessions.recordings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessions, closedSessions, recordings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Sessions {\n");
    
    sb.append("    sessions: ").append(toIndentedString(sessions)).append("\n");
    sb.append("    closedSessions: ").append(toIndentedString(closedSessions)).append("\n");
    sb.append("    recordings: ").append(toIndentedString(recordings)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

