package vti.group10.football_booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FieldCluster;

import java.util.List;
import java.util.Optional;
@Repository
public interface FieldClusterRepository extends JpaRepository<FieldCluster, Integer> {


    @Override
    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    List<FieldCluster> findAll();

    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    @Query("SELECT c FROM FieldCluster c WHERE c.visible = 'YES'")
    Page<FieldCluster> findAllVisible(Pageable pageable);

    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    @Query("SELECT c FROM FieldCluster c " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "    OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(c.district) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(c.city) LIKE LOWER(CONCAT('%', :keyword, '%')) )")
    Page<FieldCluster> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    @Query("SELECT c FROM FieldCluster c " +
            "WHERE (:address IS NULL OR LOWER(c.address) LIKE LOWER(CONCAT('%', :address, '%'))) " +
            "AND (:district IS NULL OR LOWER(c.district) LIKE LOWER(CONCAT('%', :district, '%'))) " +
            "AND (:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%')))")
    List<FieldCluster> filterByLocation(@Param("address") String address,
                                        @Param("district") String district,
                                        @Param("city") String city);

    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    List<FieldCluster> findByOwner_Id(Integer ownerId);

    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    Optional<FieldCluster> findByIdAndOwner_Id(Integer clusterId, Integer ownerId);

    @EntityGraph(attributePaths = {"fields", "images", "owner"})
    @Query("""
           SELECT c FROM FieldCluster c
           LEFT JOIN c.fields f
           LEFT JOIN c.images i
           LEFT JOIN c.owner o
           WHERE (6371 * acos(
                cos(radians(:lat)) *
                cos(radians(c.latitude)) *
                cos(radians(c.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(c.latitude))
           )) <= :radiusKm
           """)
    List<FieldCluster> findNearby(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );


}
