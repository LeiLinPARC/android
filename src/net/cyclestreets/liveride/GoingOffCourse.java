package net.cyclestreets.liveride;

import net.cyclestreets.api.Journey;
import net.cyclestreets.api.Segment;

import org.osmdroid.util.GeoPoint;

final class GoingOffCourse extends LiveRideState
{
  GoingOffCourse(final LiveRideState previous)
  {
    super(previous);
    notify("Moving away from route");
  }

  @Override
  public LiveRideState update(Journey journey, GeoPoint whereIam)
  {
    Segment nearestSeg = null;
    int distance = Integer.MAX_VALUE;
    
    for(final Segment seg : journey.segments())
    {
      int from = seg.distanceFrom(whereIam);
      if(from < distance) 
      {
        distance = from;
        nearestSeg = seg;
      } // if ...
    } // for ...
    
    if(distance > FAR_DISTANCE)
      return new ReplanFromHere(this, whereIam);
    
    if(nearestSeg != journey.activeSegment())
      return new AdvanceToSegment(this, journey, nearestSeg);

    if(distance <= NEAR_DISTANCE-5) {
      notify("Getting back on track");
      return new OnTheMove(this);
    }

    return this;
  } // update

  @Override
  public boolean isStopped() { return false; }

  @Override
  public boolean arePedalling() { return true; }
} // class GoingOffCourse
