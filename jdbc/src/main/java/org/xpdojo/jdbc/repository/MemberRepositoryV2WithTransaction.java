package org.xpdojo.jdbc.repository;

import lombok.extern.slf4j.Slf4j;
import org.xpdojo.jdbc.connection.DatabaseConnectionUtil;
import org.xpdojo.jdbc.domain.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource, JdbcUtils
 */
@Slf4j
public class MemberRepositoryV2WithTransaction {

    private final DataSource dataSource;

    public MemberRepositoryV2WithTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 사용자를 DB에 저장한다.
     *
     * @param member 사용자
     * @return 저장된 사용자 수
     * @throws SQLException
     */
    public int save(Member member) throws SQLException {
        String sql = "INSERT INTO member (member_id, money) VALUES (?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnectionUtil.getConnectionFromPool(dataSource);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getId());
            preparedStatement.setInt(2, member.getMoney());
            int rowCount = preparedStatement.executeUpdate();
            log.info("saved row count = {}", rowCount);
            return rowCount;
        } catch (SQLException e) {
            log.error(e.toString(), e);
            throw e;
        } finally {
            DatabaseConnectionUtil.closedByJdbcUtils(preparedStatement);
            assert connection != null;
            connection.close();
        }
    }

    /**
     * 사용자를 DB에서 조회한다.
     *
     * @param memberId 사용자 ID
     * @return 사용자
     * @throws SQLException
     * @throws NoSuchElementException 조회된 사용자가 없는 경우
     * @see ResultSet#next()
     * @see org.h2.jdbc.JdbcResultSet#next()
     * @see com.zaxxer.hikari.pool.HikariProxyResultSet#next()
     */
    public Member findById(String memberId) throws SQLException, NoSuchElementException {
        String sql = "select member_id, money from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnectionUtil.getConnectionFromPool(dataSource);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Member.builder()
                        .id(resultSet.getString("member_id"))
                        .money(resultSet.getInt("money"))
                        .build();
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error(e.toString(), e);
            throw e;
        } finally {
            DatabaseConnectionUtil.closeConnectionByJdbcUtils(connection, preparedStatement, resultSet);
        }
    }

    /**
     * 사용자를 DB에서 조회한다.
     *
     * @param memberId 사용자 ID
     * @return 사용자
     * @throws SQLException
     * @throws NoSuchElementException 조회된 사용자가 없는 경우
     * @see ResultSet#next()
     * @see org.h2.jdbc.JdbcResultSet#next()
     * @see com.zaxxer.hikari.pool.HikariProxyResultSet#next()
     */
    public Member findById(Connection connection, String memberId) throws SQLException, NoSuchElementException {
        String sql = "select member_id, money from member where member_id = ?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Member.builder()
                        .id(resultSet.getString("member_id"))
                        .money(resultSet.getInt("money"))
                        .build();
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error(e.toString(), e);
            throw e;
        } finally {
            DatabaseConnectionUtil.closedByJdbcUtils(preparedStatement, resultSet);
        }
    }

    public int update(Connection connection, String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int rowCount = preparedStatement.executeUpdate();
            log.info("updated row count = {}", rowCount);
            return rowCount;
        } catch (SQLException e) {
            log.error(e.toString(), e);
            throw e;
        } finally {
            DatabaseConnectionUtil.closedByJdbcUtils(preparedStatement);
        }
    }

    /**
     * 사용자를 DB에서 삭제한다.
     *
     * @param memberId 사용자 ID
     * @return 삭제된 사용자 수
     * @throws SQLException
     */
    public int delete(String memberId) throws SQLException {
        String sql = "DELETE FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnectionUtil.getConnectionFromPool(dataSource);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            int rowCount = preparedStatement.executeUpdate();
            log.info("deleted row count = {}", rowCount);
            return rowCount;
        } catch (SQLException e) {
            log.error(e.toString(), e);
            throw e;
        } finally {
            DatabaseConnectionUtil.closedByJdbcUtils(preparedStatement);
        }
    }
}
